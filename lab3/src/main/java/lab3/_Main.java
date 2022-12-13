package lab3;

import lab3.hibernate.HibernateUtil;
import lab3.hibernate.entities.Driver;
import org.hibernate.Session;

public class _Main {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("Hello world");
            Driver driver = session.get(Driver.class, 11);
//            System.out.println(driver);
            System.out.println(driver.getDriverId() + " " + driver.getLastName() + " " + driver.getFirstName() + " " + driver.getPhone());
        }
    }
}
