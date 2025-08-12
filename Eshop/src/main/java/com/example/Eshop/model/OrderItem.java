package com.example.Eshop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false) 
    private String productId;

    @Column(nullable = false) 
    private String name;

    @Column(nullable = false) 
    private double unitPrice;
    
    @Column(nullable = false) 
    private int quantity;

    // getters/setters
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public Order getOrder() { 
        return order; 
    }
    public void setOrder(Order order) { 
        this.order = order; 
    }
    public String getProductId() { 
        return productId; 
    }
    public void setProductId(String productId) {
         this.productId = productId; 
        }
    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name;
     }
    public double getUnitPrice() { 
        return unitPrice; 
    }
    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice;
     }
    public int getQuantity() { 
        return quantity; 
    }
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
    }
}
