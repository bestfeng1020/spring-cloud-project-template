package org.bestfeng.template.authorization.client;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
public interface CustomerAuthenticationManager {
    CustomerUserAuthentication getByCustomerUserId(String id);
}
