package org.bestfeng.template.authorization.client;


/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
public interface CustomerUserAuthenticationSupplier {

    CustomerUserAuthentication getByCustomerUserId(String userId);

    CustomerUserAuthentication current();
}
