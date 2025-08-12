package com.example.Eshop.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) 
    private String orderNumber;

    // customer
    @Column(nullable = false) 
    private String firstName;
    @Column(nullable = false) 
    private String lastName;
    @Column(nullable = false) 
    private String email;
    @Column(nullable = false) 
    private String phone;

    // shipping address
    @Column(nullable = false) 
    private String country;
    @Column(nullable = false) 
    private String city;
    @Column(nullable = false) 
    private String zip;
    @Column(nullable = false) 
    private String street;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingMethod shippingMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @Column(columnDefinition = "TEXT")
    private String note;

    // amounts
    @Column(nullable = false) 
    private String currency;
    @Column(nullable = false) 
    private double subtotal;
    @Column(nullable = false) 
    private double shipping;
    @Column(nullable = false) 
    private double total;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) { 
        item.setOrder(this); 
        this.items.add(item); 
    }

    // getters/setters
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getOrderNumber() { 
        return orderNumber; 
    }
    public void setOrderNumber(String orderNumber) { 
        this.orderNumber = orderNumber; 
    }
    public String getFirstName() { 
        return firstName; 
    }
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }
    public String getLastName() { 
        return lastName; 
    }
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }
    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
    public String getPhone() { 
        return phone; 
    }
    public void setPhone(String phone) { 
        this.phone = phone; 
    }
    public String getCountry() { 
        return country; 
    }
    public void setCountry(String country) { 
        this.country = country; 
    }
    public String getCity() { 
        return city; 
    }
    public void setCity(String city) { 
        this.city = city; 
    }
    public String getZip() { 
        return zip; 
    }
    public void setZip(String zip) { 
        this.zip = zip; 
    }
    public String getStreet() { 
        return street; 
    }
    public void setStreet(String street) { 
        this.street = street; 
    }
    public ShippingMethod getShippingMethod() { 
        return shippingMethod; 
    }
    public void setShippingMethod(ShippingMethod shippingMethod) { 
        this.shippingMethod = shippingMethod; 
    }
    public PaymentMethod getPaymentMethod() { 
        return paymentMethod; 
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) { 
        this.paymentMethod = paymentMethod; 
    }
    public OrderStatus getStatus() { 
        return status; 
    }
    public void setStatus(OrderStatus status) { 
        this.status = status; 
    }
    public String getNote() { 
        return note; 
    }
    public void setNote(String note) { 
        this.note = note; 
    }
    public String getCurrency() { 
        return currency; 
    }
    public void setCurrency(String currency) { 
        this.currency = currency; 
    }
    public double getSubtotal() { 
        return subtotal; 
    }
    public void setSubtotal(double subtotal) { 
        this.subtotal = subtotal; 
    }
    public double getShipping() { 
        return shipping; 
    }
    public void setShipping(double shipping) { 
        this.shipping = shipping; 
    }
    public double getTotal() { 
        return total; 
    }
    public void setTotal(double total) { 
        this.total = total; 
    }
    public OffsetDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(OffsetDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    public List<OrderItem> getItems() { 
        return items; 
    }
    public void setItems(List<OrderItem> items) { 
        this.items = items; 
    }
}
