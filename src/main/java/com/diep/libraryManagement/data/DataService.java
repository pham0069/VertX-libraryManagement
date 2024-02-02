package com.diep.libraryManagement.data;

import com.diep.libraryManagement.data.dao.BookDao;
import com.diep.libraryManagement.data.dao.BorrowDao;
import com.diep.libraryManagement.data.dao.UserDao;
import com.diep.libraryManagement.data.model.Book;
import com.diep.libraryManagement.data.model.Borrow;
import com.diep.libraryManagement.data.model.User;
import com.diep.libraryManagement.handler.RestHandlingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DataService {

  private static final long EXTEND_PERIOD =  1000L * 60 * 60 * 24 * 10; //10 days
  private static final DataService DATA_SERVICE = new DataService();
  private SessionFactory sessionFactory;
  private final BookDao bookDao;
  private final UserDao userDao;

  private final BorrowDao borrowDao;

  public static DataService getDataService() {
    return DATA_SERVICE;
  }

  public static DataService getMockDataService(SessionFactory sessionFactory,
                                               BookDao bookDao,
                                               UserDao userDao,
                                               BorrowDao borrowDao) {
    return new DataService(sessionFactory, bookDao, userDao, borrowDao);
  }

  private DataService() {
    this.sessionFactory = new Configuration().configure().buildSessionFactory();
    this.bookDao = new BookDao(sessionFactory);
    this.userDao = new UserDao(sessionFactory);
    this.borrowDao = new BorrowDao(sessionFactory);
  }

  private DataService(SessionFactory sessionFactory,
                      BookDao bookDao,
                      UserDao userDao,
                      BorrowDao borrowDao) {
    this.sessionFactory = sessionFactory;
    this.bookDao = bookDao;
    this.userDao = userDao;
    this.borrowDao = borrowDao;
  }

  public List<Book> getBooks() {
    return bookDao.getAll();
  }

  public List<User> getUsers() {
    return userDao.getAll();
  }

  private boolean isBorrowed(int bookId) {
    List<Borrow> borrows = borrowDao.getByBook(bookId);
    boolean isBorrowed = borrows.stream().anyMatch(b -> b.getReturnedDate() == null);
    return isBorrowed;
  }

  private Optional<Borrow> getActiveBorrow(int userId, int bookId) {
    List<Borrow> borrows = borrowDao.getByBookAndUser(userId, bookId);
    return borrows.stream().filter(b -> b.getReturnedDate() == null).findFirst();
  }

  public Borrow borrowBook(int userId, int bookId) throws RestHandlingException {
    User user = userDao.getById(userId);
    Book book = bookDao.getById(bookId);

    if (user == null) {
      throw new RestHandlingException("Failed to borrow. User id " + userId + " does not exist.");
    }

    if (book == null) {
      throw new RestHandlingException("Failed to borrow. Book id " + bookId + " does not exist.");
    }

    if (isBorrowed(bookId)) {
      throw new RestHandlingException("Failed to borrow. Book id " + bookId + " has been borrowed already.");
    }

    Date borrowedDate = new Date();
    Date expiryDate = getExtendDate(borrowedDate);

    Borrow borrow = new Borrow(null, user, book, borrowedDate, expiryDate, null);
    borrowDao.create(borrow);
    return borrow;
  }

  public Borrow extendBook(int userId, int bookId) throws RestHandlingException {
    User user = userDao.getById(userId);
    Book book = bookDao.getById(bookId);

    if (user == null) {
      throw new RestHandlingException("Failed to extend. User id " + userId + " does not exist.");
    }

    if (book == null) {
      throw new RestHandlingException("Failed to extend. Book id " + bookId + " does not exist.");
    }

    Optional<Borrow> activeBorrow = getActiveBorrow(userId, bookId);
    if (activeBorrow.isEmpty()) {
      throw new RestHandlingException("Failed to extend. User id " + userId + " is not borrowing book id " + bookId);
    }

    Borrow borrow = activeBorrow.get();
    Date currentExpiryDate = borrow.getExpiredDate();
    Date newExpiryDate = getExtendDate(currentExpiryDate);
    borrow.setExpiredDate(newExpiryDate);

    borrowDao.update(borrow);
    return borrow;
  }

  public Borrow returnBook(int userId, int bookId) throws RestHandlingException {
    User user = userDao.getById(userId);
    Book book = bookDao.getById(bookId);

    if (user == null) {
      throw new RestHandlingException("Failed to return. User id " + userId + " does not exist.");
    }

    if (book == null) {
      throw new RestHandlingException("Failed to return. Book id " + bookId + " does not exist.");
    }

    Optional<Borrow> activeBorrow = getActiveBorrow(userId, bookId);
    if (activeBorrow.isEmpty()) {
      throw new RestHandlingException("Failed to return. User id " + userId + " is not borrowing book id " + bookId);
    }

    Borrow borrow = activeBorrow.get();
    borrow.setReturnedDate(new Date());
    borrowDao.update(borrow);
    return borrow;
  }

  private Date getExtendDate(Date date) {
    return new Date(date.getTime() + EXTEND_PERIOD);
  }
}
