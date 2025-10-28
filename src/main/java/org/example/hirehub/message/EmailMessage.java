package org.example.hirehub.message;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {
    String to;
    String subject;
    String body;
    String type;
    boolean isHtml;

    private EmailMessage(Builder builder) {
        this.to = builder.to;
        this.subject = builder.subject;
        this.body = builder.body;
        this.type = builder.type;
        this.isHtml = builder.isHtml;

    }
    public static class Builder {
        private String to;
        private String subject;
        private String body;
        private String type;
        private boolean isHtml;
        public Builder to(String to) {
            this.to = to;
            return this;
        }
        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }
        public Builder body(String body) {
            this.body = body;
            return this;
        }
        public Builder type(String type) {
            this.type = type;
            return this;

        }
        public Builder isHtml(boolean isHtml) {
            this.isHtml = isHtml;
            return this;
        }
        public EmailMessage build() {
            return new EmailMessage(this);
        }

    }
}
