package com.tistory.amyyzzin.order.client.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendMailForm {

    private String from;
    private String to;
    private String subject;
    private String text;

}
