package com.diep.libraryManagement.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="borrows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Borrow {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;
  @Column(name = "borrowed")
  private Date borrowedDate;

  @Column(name = "expired")
  private Date expiredDate;
  @Column(name = "returned")
  private Date returnedDate;
}
