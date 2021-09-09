package ru.ravilov.PasrseXMLApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.ravilov.PasrseXMLApp.model.Employee;
import ru.ravilov.PasrseXMLApp.repository.EmployeeRepository;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс с бизнес-логикой
 */

@Service
public class EmployeeService {
    EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    /**
     * Преобразуем Xml-данные из файла в обьект Java
     * и загружаем в БД
     */
    public void getParseFromXml(String filename) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new File(filename));
                doc.getDocumentElement().normalize();

                NodeList list = doc.getElementsByTagName("employee");

                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String depCode = element.getElementsByTagName("depCode").item(0).getTextContent();
                        String depJob = element.getElementsByTagName("depJob").item(0).getTextContent();
                        String description = element.getElementsByTagName("description").item(0).getTextContent();
                       Employee employee = new Employee(depCode,depJob,description);
                        employeeRepository.save(employee);
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
    }
    /**
     * Отдельный метод для преобразования данных
     * из Xml в Java-обьект
     * используется в методе syncDbWithFile()
     */
    public List<Employee> getParseXmlToList(String filename){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Employee employee = null;
        List<Employee> employeeList = new ArrayList<>();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filename));
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("employee");

            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String depCode = element.getElementsByTagName("depCode").item(0).getTextContent();
                    String depJob = element.getElementsByTagName("depJob").item(0).getTextContent();
                    String description = element.getElementsByTagName("description").item(0).getTextContent();
                     employee = new Employee(depCode,depJob,description);
                     employeeList.add(employee);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return employeeList;
    }
    /**
     * Экспортируем данные из базы данных
     * и загружаем в Xml-файл
     */
    public void exportFromDbToXml(String filename) throws IOException { ;
        FileWriter writer = new FileWriter(new File(filename + ".xml"));
        writer.write("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>\n");
        writer.write("<employees>\n");
        List<Employee> employeeList = employeeRepository.findAll();
        for (Employee s: employeeList){
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Employee.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
                jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                jaxbMarshaller.marshal(s,writer);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        writer.write("\n</employees>");
        writer.flush();
    }
    /**
     * Синхронизируем xml-файл с базой данных
     */
    public void syncDbWithFile(String fileXml) throws SQLException, ParserConfigurationException, SAXException, IOException {
        //Данные из файла
        Map<Integer,Employee> mapFromFile = new HashMap<>();
        //Данные из БД
        Map<Integer,Employee> mapFromDb = new HashMap<>();
        List<Employee> arrayToDelete = new ArrayList<>();
        List<Employee> arrayToInsert = new ArrayList<>();
        List<Employee> arrayToUpdate = new ArrayList<>();
        List<Employee> extractFromDbList = employeeRepository.findAll();
        List<Employee> extractFromFile = getParseXmlToList(fileXml);
        for (Employee employee : extractFromDbList){
            System.out.println(employee);
        }
        // Достаем данные из БД и добавляем в map
        for (Employee employee : extractFromDbList){
            mapFromDb.put(employee.hashCode(),employee);
        }
        //Достаем данные из файла и добавляем в map
        for (Employee employee : extractFromFile){
            if (mapFromFile.get(employee.hashCode()) != null){
                continue;
            }
            mapFromFile.put(employee.hashCode(),employee);
        }

        for (Integer employee : mapFromDb.keySet()){
            if (mapFromFile.get(employee) == null){
                Employee emp = mapFromDb.get(employee);
                arrayToDelete.add(emp);
            } else {
                Employee ffEmp = mapFromFile.get(employee);
                Employee fbEmp = mapFromDb.get(employee);
                if (!ffEmp.getDescription().equals(fbEmp.getDescription())){
                    ffEmp.setId(fbEmp.getId());
                    arrayToUpdate.add(ffEmp);
                }
            }
        }
        //ищем новые
        for (Integer employee : mapFromFile.keySet()){
            if (mapFromDb.get(employee) == null){
                arrayToInsert.add(mapFromFile.get(employee));
            }
        }
        // если массив не пуст удаляем данные
        if (!arrayToDelete.isEmpty()) {
            AppRunner.log.info("Удаление записей");
            for (Employee employee : arrayToDelete){
                System.out.println(employee);
                employeeRepository.deleteByDepJob(employee);
            }
        }
        // если массив не пуст обновляем данные
        if (!arrayToUpdate.isEmpty()) {
            AppRunner.log.info("Обновление записей");
            for (Employee employee : arrayToUpdate){
                System.out.println(employee);
                employeeRepository.updateByList(employee.getId());
            }
        }
        // если массив не пуст добавляем данные
        if (!arrayToInsert.isEmpty()) {
            AppRunner.log.info("Добавление записей");
            for (Employee employee : arrayToInsert) {
                employeeRepository.save(employee);
            }
        }
    }
}
