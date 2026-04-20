package com.eems.model;

import java.util.ArrayList;
import java.util.List;

public class Client extends BaseEntity {

    private String name;
    private String industry;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private List<Project> projects = new ArrayList<>();

    public Client() {
    }

    public Client(String name, String industry, String contactName,
                  String contactPhone, String contactEmail) {
        this.name = name;
        this.industry = industry;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    public Client(int id, String name, String industry, String contactName,
                  String contactPhone, String contactEmail) {
        this.id = id;
        this.name = name;
        this.industry = industry;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void addProject(Project project) {
        this.projects.add(project);
    }

    @Override
    public String toString() {
        return String.format("Client{id=%d, name='%s', industry='%s', contactName='%s', contactEmail='%s'}",
                id, name, industry, contactName, contactEmail);
    }
}
