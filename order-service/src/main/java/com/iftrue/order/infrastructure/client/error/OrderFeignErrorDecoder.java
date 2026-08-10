package com.iftrue.order.infrastructure.client.error;

import com.iftrue.order.global.exception.BusinessException;
import com.iftrue.order.global.exception.OrderErrorCode;
import feign.Response;
import feign.codec.ErrorDecoder;

public class OrderFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        OrderErrorCode errorCode = resolveErrorCode(methodKey, response.status());

        return new BusinessException(errorCode);
    }

    private OrderErrorCode resolveErrorCode(String methodKey, int status) {
        if (methodKey.contains("DeliveryClient#createDelivery")) {
            return OrderErrorCode.DELIVERY_CREATION_FAILED;
        }

        if (methodKey.contains("ProductClient#restoreInventory")) {
            return OrderErrorCode.INVENTORY_RESTORE_FAILED;
        }

        if (status == 404
                && methodKey.contains("CompanyClient#checkCompanyExists")) {
            return OrderErrorCode.COMPANY_NOT_FOUND;
        }

        if (status == 404
                && methodKey.contains("ProductClient#getProduct")) {
            return OrderErrorCode.PRODUCT_NOT_FOUND;
        }

        if (status == 404
                && methodKey.contains("UserClient#getUser")) {
            return OrderErrorCode.RECIPIENT_NOT_FOUND;
        }

        if (status == 409
                && methodKey.contains("ProductClient#decreaseInventory")) {
            return OrderErrorCode.INSUFFICIENT_PRODUCT_STOCK;
        }

        return OrderErrorCode.EXTERNAL_SERVICE_CALL_FAILED;
    }
}
