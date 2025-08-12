package com.pei.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

//manyToOne con usuario
@Entity
public record Purchase(@Id Long id, Long userId) {}
