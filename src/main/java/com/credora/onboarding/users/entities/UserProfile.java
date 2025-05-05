//package com.credora.onboarding.users.entities;
//
//import com.credora.onboarding.users.enums.EmploymentStatus;
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Getter
//@Setter
//@Entity
//@Table(name = "profiles")
//public class UserProfile {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.UUID)
//  @Column()
//  private UUID id;
//
//  @Column()
//  private String bvn;
//
//  @Enumerated(EnumType.STRING)
//  @Column(nullable = false)
//  private EmploymentStatus employmentStatus;
//
//  @Column()
//  private BigDecimal annualSalary;
//
//  @Column()
//  private BigDecimal incomeProof;
//
//  @Column
//  private String address;
//
//  @OneToOne(mappedBy = "profile")
//  private User user;
//
//  @CreationTimestamp
//  @Column(nullable = false, updatable = false)
//  private LocalDateTime createdAt;
//
//  @UpdateTimestamp
//  @Column(nullable = false)
//  private LocalDateTime updatedAt;
//}
