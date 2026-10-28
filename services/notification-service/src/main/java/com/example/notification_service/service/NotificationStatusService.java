package com.example.notification_service.service;

import com.example.notification_service.enums.NotificationStatus;
import org.springframework.stereotype.Service;

@Service
public class NotificationStatusService {
    public boolean isValidTransition(String currentStatus, String newStatus){
//        boolean validTransition=true;
//        if(currentStatus.equals(NotificationStatus.PENDING.name())){
//            if (newStatus.equals(NotificationStatus.PENDING.name()) || newStatus.equals(NotificationStatus.READ.name())) {
//                validTransition = false;
//            }
//        }
//        if(currentStatus.equals(NotificationStatus.SENT.name())){
//            if(!newStatus.equals(NotificationStatus.READ.name())){
//                validTransition=false;
//            }
//        }
//        if(currentStatus.equals(NotificationStatus.FAILED.name())){
//            if (!newStatus.equals(NotificationStatus.PENDING.name())){
//                validTransition=false;
//            }
//        }
//        if(currentStatus.equals(NotificationStatus.READ.name())){
//            validTransition=false;
//        }
//        return validTransition;

        boolean validTransition=false;
        if(currentStatus.equals(NotificationStatus.PENDING.name())){
            if (newStatus.equals(NotificationStatus.SENT.name()) || newStatus.equals(NotificationStatus.FAILED.name())) {
                validTransition = true;
            }
        }
        if(currentStatus.equals(NotificationStatus.SENT.name())){
            if(newStatus.equals(NotificationStatus.READ.name())){
                validTransition=true;
            }
        }
        if(currentStatus.equals(NotificationStatus.FAILED.name())){
            if (newStatus.equals(NotificationStatus.PENDING.name())){
                validTransition=true;
            }
        }
//        if(currentStatus.equals(NotificationStatus.READ.name())){
//            validTransition=true;
//        }
        return validTransition;
    }
}
