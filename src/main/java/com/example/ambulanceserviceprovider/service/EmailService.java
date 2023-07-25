package com.example.ambulanceserviceprovider.service;

import com.example.ambulanceserviceprovider.dto.request.EmailDetails;

public interface EmailService {

    void sendEmail(EmailDetails emailDetails);
}
