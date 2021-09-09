package ru.ravilov.PasrseXMLApp.service;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Scanner;


@Component
public class AppRunner implements CommandLineRunner {
    public final static Logger log = Logger.getLogger(AppRunner.class);
    EmployeeService employeeService;
    @Autowired
    public AppRunner(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public void run(String... args) throws Exception {
        start();
}

    public void start() throws IOException{
            System.out.printf("Добро пожаловать! Выберите желаемое действие:\n " +
                    "1)Загрузить файл в Бд \n 2)Выгрузить файл из БД \n 3)Синхронизировать Бд с выбранным файлом \n");
            System.out.print("Ваш выбор: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String select = reader.readLine();
            if (select.equals("1")) {
                System.out.print("Укажите полный путь к xml файлу: ");
                String filename = reader.readLine();
                employeeService.getParseFromXml(filename);
                AppRunner.log.info("Выполнение преобразования");
            } else if (select.equals("2")) {
                System.out.print("Укажите имя файла(без расширения): ");
                    String filename = reader.readLine();
                    employeeService.exportFromDbToXml(filename);
            } else if (select.equals("3")) {
                System.out.print("Укажите путь к файлу: ");
                String filename = reader.readLine();
                try {
                    employeeService.syncDbWithFile(filename);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                    throw new RuntimeException("Выход из программы!");
                }
                catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
    }
}
