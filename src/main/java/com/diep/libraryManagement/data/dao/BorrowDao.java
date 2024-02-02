package com.diep.libraryManagement.data.dao;

import com.diep.libraryManagement.data.model.Borrow;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BorrowDao {
  private static final Logger LOG = LoggerFactory.getLogger(BorrowDao.class);

  private final SessionFactory sessionFactory;

  public BorrowDao(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
  public void create(Borrow borrow) {
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      session.save(borrow);
      session.getTransaction().commit();
      LOG.debug("Added borrow session " + borrow.getId());
    } catch (RuntimeException e) {
      session.getTransaction().rollback();
      LOG.error("Error adding borrow", e);
    } finally {
      session.flush();
      session.close();
    }
  }

  public void update(Borrow borrow) {
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      session.update(borrow);
      session.getTransaction().commit();
      LOG.debug("Updated borrow " + borrow.getId());
    } catch (RuntimeException e) {
    session.getTransaction().rollback();
      LOG.error("Error updating borrow", e);
    } finally {
      session.flush();
      session.close();
    }
  }

  public void delete(int id) {
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      Borrow borrow = session.load(Borrow.class, id);
      session.delete(borrow);
      session.getTransaction().commit();
      LOG.debug("Deleted borrow " + borrow.getId());
    } catch (RuntimeException e) {
      session.getTransaction().rollback();
      LOG.error("Error deleting borrow", e);
    } finally {
      session.flush();
      session.close();
    }
  }


  public List<Borrow> getAll() {
    Session session = sessionFactory.openSession();
    return session.createQuery("FROM Borrow").list();
  }

  public Borrow getById(int id) {
    Session session = sessionFactory.openSession();
    return session.load(Borrow.class, id);
  }

  public List<Borrow> getByBook(int bookId) {
    Session session = sessionFactory.openSession();
    return session.createQuery("FROM Borrow WHERE book_id = :bookId")
      .setParameter("bookId", bookId).list();
  }

  public List<Borrow> getByBookAndUser(int userId, int bookId) {
    Session session = sessionFactory.openSession();
    return session.createQuery("FROM Borrow WHERE user_id = :userId AND book_id = :bookId")
      .setParameter("userId", userId)
      .setParameter("bookId", bookId).list();
  }
}
