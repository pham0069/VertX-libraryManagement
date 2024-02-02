package com.diep.libraryManagement.data.dao;

import com.diep.libraryManagement.data.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDao {
  private static final Logger LOG = LoggerFactory.getLogger(UserDao.class);

  private final SessionFactory sessionFactory;

  public UserDao(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
  public void create(User user) {
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      session.save(user);
      session.getTransaction().commit();
      LOG.debug("Added user " + user.getId());
    } catch (RuntimeException e) {
      session.getTransaction().rollback();
      LOG.error("Error adding user", e);
    } finally {
      session.flush();
      session.close();
    }
  }

  public void update(User user) {
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      session.update(user);
      session.getTransaction().commit();
      LOG.debug("Updated user " + user.getId());
    } catch (RuntimeException e) {
      session.getTransaction().rollback();
      LOG.error("Error updating user", e);
    } finally {
      session.flush();
      session.close();
    }
  }

  public void delete(int id) {
    Session session = sessionFactory.openSession();
    try {
      session.beginTransaction();
      User user = session.load(User.class, id);
      session.delete(user);
      session.getTransaction().commit();
      LOG.debug("Deleted user " + user.getId());
    } catch (RuntimeException e) {
      session.getTransaction().rollback();
      LOG.error("Error deleting user", e);
    } finally {
      session.flush();
      session.close();
    }
  }


  public List<User> getAll() {
    Session session = sessionFactory.openSession();
    return session.createQuery("FROM User").list();
  }

  public User getById(int id) {
    Session session = sessionFactory.openSession();
    return session.load(User.class, id);
  }

  public List<User> getByName(String name) {
    Session session = sessionFactory.openSession();
    return session.createQuery("FROM User WHERE name = :name")
      .setParameter("name", name).list();
  }
}
