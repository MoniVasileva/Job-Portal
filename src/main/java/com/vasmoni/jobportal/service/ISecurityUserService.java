package com.vasmoni.jobportal.service;

public interface ISecurityUserService {

    String validatePasswordResetToken(String token);

}
