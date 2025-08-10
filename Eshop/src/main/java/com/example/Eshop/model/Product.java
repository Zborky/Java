package com.example.Eshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "products") 
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private Double price;
    private String category;
    private String imagePath;

    public Product (){}

     public Product(String name, String description, Double price, String category, String imagePath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    // Gettery a settery
    public Long getId() { 
        return id;
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getName() { 
        return name;
    }
    public void setName(String name) { 
        this.name = name; 
    }

    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }

    public Double getPrice() { 
        return price; 
    }
    public void setPrice(Double price) { 
        this.price = price; 
    
    }

    public String getCategory() { 
        return category; 
    }
    public void setCategory(String category) { 
        this.category = category; 
    }

    public String getImagePath() { 
        return imagePath; 
    }
    public void setImagePath(String imagePath) { 
        this.imagePath = imagePath; 
    }

}
