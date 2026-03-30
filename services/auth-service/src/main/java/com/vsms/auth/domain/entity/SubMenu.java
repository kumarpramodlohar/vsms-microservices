package com.vsms.auth.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * SubMenu entity mapped to adm_sub_menus table.
 */
@Entity
@Table(name = "adm_sub_menus")
@Getter
@Setter
public class SubMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "sub_menu_name", length = 100)
    private String subMenuName;

    @Column(name = "sub_menu_url", length = 255)
    private String subMenuUrl;

    @Column(name = "sub_menu_icon", length = 50)
    private String subMenuIcon;

    @Column(name = "order_by")
    private Integer orderBy;

    @Column(name = "active", length = 1)
    private String active = "Y";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Version
    @Column(name = "version")
    private Long version = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", insertable = false, updatable = false)
    private Menu menu;
}
