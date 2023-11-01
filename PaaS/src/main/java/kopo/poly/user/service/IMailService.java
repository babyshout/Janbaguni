package kopo.poly.user.service;

import kopo.poly.user.dto.MailDTO;

public interface IMailService {

    // 메일 발송
    int doSendMail(MailDTO pDTO);
}
