package org.example.hirehub.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import org.example.hirehub.dto.chatbot.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiChatService {

    private static final Logger log = LoggerFactory.getLogger(GeminiChatService.class);

    private final GoogleAiGeminiStreamingChatModel streamingModel;
    private final RAGService ragService;
    @Value("${frontend.url}")
    private String feUrl;

    private static final String SYSTEM_PROMPT = """
            Bạn là Hubby - trợ lý tuyển dụng thông minh của HireHub.
            Nhiệm vụ của bạn:
            - Hỗ trợ ứng viên tìm việc làm phù hợp
            - Phân tích và đánh giá CV/Resume
            - Tư vấn nghề nghiệp và phỏng vấn
            - Trả lời các câu hỏi về việc làm

            Nguyên tắc:
            - Luôn trả lời bằng tiếng Việt
            - Thân thiện, chuyên nghiệp và hữu ích
            - Đưa ra lời khuyên cụ thể và thiết thực
            - Khi giới thiệu việc làm:
              * Luôn đề cập ID của công việc
              * Bọc **tiêu đề công việc** thành link có thể click
              * Link có dạng: %s/job-details/{jobId}
                            

            Quy tắc định dạng (BẮT BUỘC):
            - Sử dụng Markdown để format text
            - LUÔN LUÔN thêm 2 dòng trống trước mỗi heading (##, ###)
            - LUÔN LUÔN thêm 1 dòng trống trước mỗi bullet point list
            - Mỗi bullet point phải ở một dòng riêng biệt
            - Dùng ** để bold text quan trọng
            - Dùng - hoặc * cho danh sách, mỗi item một dòng
            - Ví dụ đúng:

            ## Điểm mạnh

            * Item 1
            * Item 2

            ## Điểm cần cải thiện

            * Item A
            * Item B
            
            """;

    public GeminiChatService(GoogleAiGeminiStreamingChatModel streamingModel, RAGService ragService) {
        this.streamingModel = streamingModel;
        this.ragService = ragService;
    }

    /**
     * Stream chat với conversation history và RAG context (tự động detect khi cần)
     * Xử lý tất cả: phân tích CV, tìm việc, tìm ứng viên
     */
    public Flux<String> streamChat(String message, List<MessageDTO> history) {
        // Auto-detect query type
        boolean isJobQuery = isJobRelatedQuery(message);
        boolean isUserQuery = isUserRelatedQuery(message);

        String ragContext = "";
        try {
            if (isJobQuery || isUserQuery) {
                ragContext = ragService.buildContext(message, isJobQuery, isUserQuery);
                if (!ragContext.isEmpty()) {
                    log.info("Added RAG context (jobs={}, users={}) for: {}",
                            isJobQuery, isUserQuery,
                            message.substring(0, Math.min(50, message.length())));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get RAG context: {}", e.getMessage());
        }

        List<ChatMessage> messages = buildMessages(history, message, ragContext);

        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();
            int FLUSH_SIZE = 40; // characters

            streamingModel.generate(messages, new StreamingResponseHandler<AiMessage>() {
                @Override
                public void onNext(String token) {
                    buffer.append(token);

                    if (buffer.length() >= FLUSH_SIZE) {
                        sink.next(buffer.toString());
                        buffer.setLength(0);
                    }
                }

                @Override
                public void onComplete(Response<AiMessage> response) {

                    if (!buffer.isEmpty()) {
                        sink.next(buffer.toString());
                    }
                    sink.complete();
                }

                @Override
                public void onError(Throwable error) {
                    sink.error(error);
                }
            });
        });
    }

    /**
     * Stream chat với file upload (PDF) - không áp dụng RAG vì đây là phân tích CV
     */
    public Flux<String> streamChatWithFile(String message, List<MessageDTO> history, MultipartFile file) {
        String fileContent = parsePdfToText(file);

        String enhancedMessage = String.format("""
                📄 File được upload: %s

                === NỘI DUNG FILE ===
                %s
                === KẾT THÚC FILE ===

                📝 Yêu cầu của người dùng: %s
                """,
                file.getOriginalFilename(),
                fileContent,
                message);

        // For file uploads, streamChat will auto-detect if it's job-related
        return streamChat(enhancedMessage, history);
    }

    /**
     * Build messages list với system prompt, RAG context và history
     */
    private List<ChatMessage> buildMessages(List<MessageDTO> history, String currentMessage, String ragContext) {
        List<ChatMessage> messages = new ArrayList<>();

        // Add system prompt with RAG context
        String fullSystemPrompt = String.format(SYSTEM_PROMPT, feUrl);
        if (ragContext != null && !ragContext.isEmpty()) {
            fullSystemPrompt = String.format(SYSTEM_PROMPT, feUrl) + ragContext;
        }
        messages.add(SystemMessage.from(fullSystemPrompt));

        // Add conversation history
        if (history != null) {
            for (MessageDTO msg : history) {
                if ("user".equalsIgnoreCase(msg.getRole())) {
                    messages.add(UserMessage.from(msg.getContent()));
                } else if ("assistant".equalsIgnoreCase(msg.getRole())) {
                    messages.add(AiMessage.from(msg.getContent()));
                }
            }
        }

        // Add current user message
        messages.add(UserMessage.from(currentMessage));

        return messages;
    }

    /**
     * Parse PDF file to text
     */
    private String parsePdfToText(MultipartFile file) {
        try {
            ApachePdfBoxDocumentParser parser = new ApachePdfBoxDocumentParser();
            InputStream inputStream = file.getInputStream();
            Document document = parser.parse(inputStream);
            return document.text();
        } catch (Exception e) {
            throw new RuntimeException("Không thể đọc file PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Detect if message is job-related using keywords
     */
    private boolean isJobRelatedQuery(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        String lowerMessage = message.toLowerCase();

        // Vietnamese job-related keywords
        String[] jobKeywords = {
                "tìm việc", "việc làm", "công việc", "tuyển dụng",
                "job", "developer", "engineer", "lập trình", "intern",
                "fresher", "senior", "junior", "remote", "fulltime", "parttime",
                "backend", "frontend", "fullstack", "devops", "data",
                "lương", "salary", "vị trí", "ứng tuyển", "apply",
                "có việc", "cần tuyển", "đang tuyển", "mức lương"
        };

        for (String keyword : jobKeywords) {
            if (lowerMessage.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Detect if message is user/candidate-related using keywords
     */
    private boolean isUserRelatedQuery(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        String lowerMessage = message.toLowerCase();

        // Vietnamese user/candidate-related keywords
        String[] userKeywords = {
                "tìm ứng viên", "ứng viên", "candidate", "người", "nhân viên",
                "tìm người", "ai", "who", "team", "developer nào",
                "engineer nào", "lập trình viên", "tuyển ai",
                "hồ sơ", "profile", "cv", "resume", "kinh nghiệm",
                "kỹ năng", "skill", "java", "python", "javascript", "react",
                "nodejs", "spring", "php", ".net", "golang", "ruby"
        };

        for (String keyword : userKeywords) {
            if (lowerMessage.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}
