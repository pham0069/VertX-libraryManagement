package com.diep.libraryManagement.data.dao;

import com.diep.libraryManagement.data.model.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BookDao {
  private static final Logger LOG = LoggerFactory.getLogger(BookDao.class);

  private final SessionFactory sessionFactory;

  public BookDao(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
  public void create(Book book) {
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      session.save(book);
      session.getTransaction().commit();
      LOG.debug("Added book " + book.getId());
    } catch (RuntimeException e) {
      session.getTransaction().rollback();
      LOG.error("Error creating book", e);
    } finally {
      session.flush();
      session.close();
    }
  }

  public void update(Book book) {
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      session.update(book);
      session.getTransaction().commit();
      LOG.debug("Updated book " + book.getId());
    } catch (RuntimeException e) {
      session.getTransaction().rollback();
      LOG.error("Error updating book", e);
    } finally {
      session.flush();
      session.close();
    }
  }

  public void delete(int id) {
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      Book book = session.load(Book.class, id);
      session.delete(book);
      session.getTransaction().commit();
      LOG.debug("Deleted book " + book.getId());
    } catch (RuntimeException e) {
      session.getTransaction().rollback();
      LOG.error("Error deleting book", e);
    } finally {
      session.flush();
      session.close();
    }
  }


  public List<Book> getAll() {
    Session session = sessionFactory.openSession();
    return session.createQuery("FROM Book").list();
  }

  public Book getById(int id) {
    Session session = sessionFactory.openSession();
    return session.load(Book.class, id);
  }

  public List<Book> getByIsbn(String isbn) {
    Session session = sessionFactory.openSession();
    return session.createQuery("FROM Book WHERE isbn = :isbn")
      .setParameter("isbn", isbn).list();
  }
}
