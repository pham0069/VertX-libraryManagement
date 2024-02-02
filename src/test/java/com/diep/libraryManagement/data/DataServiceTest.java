package com.diep.libraryManagement.data;


import com.diep.libraryManagement.data.dao.BookDao;
import com.diep.libraryManagement.data.dao.BorrowDao;
import com.diep.libraryManagement.data.dao.UserDao;
import com.diep.libraryManagement.data.model.Book;
import com.diep.libraryManagement.data.model.Borrow;
import com.diep.libraryManagement.data.model.User;
import com.diep.libraryManagement.handler.RestHandlingException;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DataServiceTest {
  @Mock
  SessionFactory sessionFactory;
  @Mock
  UserDao userDao;
  @Mock
  BookDao bookDao;
  @Mock
  BorrowDao borrowDao;

  DataService dataService;
  List<Book> books = new ArrayList<>();
  @Mock
  Book book1;
  @Mock
  Book book2;

  List<User> users = new ArrayList<>();
  @Mock
  User user1;
  @Mock
  User user2;
  List<Borrow> borrows = new ArrayList<>();
  @Mock Borrow borrow1;
  @Mock Date borrowedDate;
  @Mock Date expiredDate;
  @Captor
  ArgumentCaptor<Boolean> isBorrowedCaptor;

  @BeforeEach
  void setup() {
    setupBookDao();
    setupUserDao();
    setupBorrowDao();

    dataService = DataService.getMockDataService(sessionFactory, bookDao, userDao, borrowDao);
  }

  private void setupBookDao() {
    when(book1.getId()).thenReturn(1);
    when(book2.getId()).thenReturn(2);
    books.add(book1);
    books.add(book2);

    when(bookDao.getById(1)).thenReturn(book1);
    when(bookDao.getById(2)).thenReturn(book2);
    when(bookDao.getAll()).thenReturn(books);
  }

  private void setupUserDao() {
    when(user1.getId()).thenReturn(1);
    when(user2.getId()).thenReturn(2);
    users.add(user1);
    users.add(user2);

    when(userDao.getById(1)).thenReturn(user1);
    when(userDao.getById(2)).thenReturn(user1);
    when(userDao.getAll()).thenReturn(users);
  }

  private void setupBorrowDao() {
    when(borrow1.getUser()).thenReturn(user1);
    when(borrow1.getBook()).thenReturn(book1);
    when(borrow1.getBorrowedDate()).thenReturn(borrowedDate);
    when(borrow1.getExpiredDate()).thenReturn(expiredDate);
    when(borrow1.getReturnedDate()).thenReturn(null);
    borrows.add(new Borrow(1, user1, book1, new Date(), new Date(), null));

    when(borrowDao.getByBook(1)).thenReturn(borrows);
    when(borrowDao.getByBookAndUser(1,1)).thenReturn(borrows);
  }

  @Test
  void test_get_all_books() {
    List<Book> actualBooks = dataService.getBooks();

    verify(bookDao).getAll();

    assertEquals(books.size(), actualBooks.size());
    for (int i = 0; i < books.size(); i++) {
      assertEquals(books.get(i), actualBooks.get(i));
    }
  }

  @Test
  void test_get_all_users() {
    List<User> actualUsers = dataService.getUsers();

    verify(userDao).getAll();

    assertEquals(users.size(), actualUsers.size());
    for (int i = 0; i < users.size(); i++) {
      assertEquals(users.get(i), actualUsers.get(i));
    }
  }

  @Test
  void test_borrow_succeed() throws RestHandlingException {
    Borrow borrow = dataService.borrowBook(1, 2);

    verify(bookDao).getById(2);
    verify(userDao).getById(1);
    verify(borrowDao).getByBook( 2);

    assertEquals(borrow.getUser(), user1);
    assertEquals(borrow.getBook(), book2);
  }

  @Test
  void test_borrow_fail_if_user_does_not_exist() throws RestHandlingException {
    Throwable exception = assertThrows(RestHandlingException.class, () -> dataService.borrowBook(5, 1));
    assertEquals(exception.getMessage(), "Failed to borrow. User id 5 does not exist.");
  }

  @Test
  void test_borrow_fail_if_book_does_not_exist() throws RestHandlingException {
    Throwable exception = assertThrows(RestHandlingException.class, () -> dataService.borrowBook(1, 3));
    assertEquals(exception.getMessage(), "Failed to borrow. Book id 3 does not exist.");
  }

  @Test
  void test_borrow_fail_if_book_has_been_borrowed() throws RestHandlingException {
    Throwable exception = assertThrows(RestHandlingException.class, () -> dataService.borrowBook(2, 1));
    assertEquals(exception.getMessage(), "Failed to borrow. Book id 1 has been borrowed already.");
  }

  @Test
  void test_extend_succeed() throws RestHandlingException {
    Borrow borrow = dataService.extendBook(1, 1);
    assertEquals(borrow.getUser(), user1);
    assertEquals(borrow.getBook(), book1);
  }
  @Test
  void test_extend_fail_if_book_is_not_being_borrowed_by_user() throws RestHandlingException {
    Throwable exception = assertThrows(RestHandlingException.class, () -> dataService.extendBook(2, 1));
    assertEquals(exception.getMessage(), "Failed to extend. User id 2 is not borrowing book id 1");
  }

  @Test
  void test_return_succeed() throws RestHandlingException {
    Borrow borrow = dataService.returnBook(1, 1);
    assertEquals(borrow.getUser(), user1);
    assertEquals(borrow.getBook(), book1);
    assertNotNull(borrow.getReturnedDate());
  }

  @Test
  void test_return_fail_if_book_is_not_borrowed() throws RestHandlingException {
    Throwable exception = assertThrows(RestHandlingException.class, () -> dataService.returnBook(1, 2));
    assertEquals(exception.getMessage(), "Failed to return. User id 1 is not borrowing book id 2");
  }

}
