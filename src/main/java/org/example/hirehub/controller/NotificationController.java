//package org.example.hirehub.controller;
//
//import com.google.firebase.messaging.FirebaseMessagingException;
//import org.example.hirehub.service.FirebaseNotificationService;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/notify")
//public class NotificationController {
//
//    private final FirebaseNotificationService service;
//
//    public NotificationController(FirebaseNotificationService service) {
//        this.service = service;
//    }
//
//    @PostMapping
//    public String send(@RequestParam String token) throws FirebaseMessagingException {
//        return service.sendNotification(
//                token,
//                "HireHub",
//                "Bạn có thông báo mới"
//        );
//    }
//
//}
