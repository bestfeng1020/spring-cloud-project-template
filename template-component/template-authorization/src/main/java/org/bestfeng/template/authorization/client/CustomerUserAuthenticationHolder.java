package org.bestfeng.template.authorization.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
public class CustomerUserAuthenticationHolder {

    static List<CustomerUserAuthenticationSupplier> suppliers = new ArrayList<>();

    static void registerSupplier(CustomerUserAuthenticationSupplier supplier) {
        suppliers.add(supplier);
    }

    public static CustomerUserAuthentication getCurrent() {
        return suppliers.stream()
                .map(CustomerUserAuthenticationSupplier::current)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

    }

    public static CustomerUserAuthentication getByUserId(String userId) {
        if (userId == null) {
            return null;
        }
        return suppliers.stream()
                .map(supplier -> supplier.getByCustomerUserId(userId))
                .findFirst()
                .orElse(null);

    }
}
