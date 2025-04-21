package com.urbanfood.ecommerce.entity;

public class SupplierLoginResponseDTO {
    private Long supplierId;
    private String message;

    public SupplierLoginResponseDTO(Long supplierId, String message) {
        this.supplierId = supplierId;
        this.message = message;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
