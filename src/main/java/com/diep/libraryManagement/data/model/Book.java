package com.diep.libraryManagement.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="books")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Book {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "code")
  private String code;
  @Column(name = "isbn")
  private String isbn;
}
