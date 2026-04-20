package com.eems.service;

import com.eems.model.Client;
import com.eems.model.Project;
import com.eems.repository.ClientRepository;
import com.eems.repository.ProjectRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientService {

    private final ClientRepository clientRepo;
    private final ProjectRepository projectRepo;

    public ClientService() {
        this.clientRepo = new ClientRepository();
        this.projectRepo = new ProjectRepository();
    }

    public Client create(Client client) throws SQLException {
        if (client.getName() == null || client.getName().isBlank())
            throw new IllegalArgumentException("Client name cannot be empty.");
        return clientRepo.create(client);
    }

    public Client findById(int id) throws SQLException {
        return clientRepo.findById(id);
    }

    public List<Client> findAll() throws SQLException {
        return clientRepo.findAll();
    }

    public void update(Client client) throws SQLException {
        if (clientRepo.findById(client.getId()) == null)
            throw new IllegalArgumentException("Client not found: " + client.getId());
        clientRepo.update(client);
    }

    public void delete(int id) throws SQLException {
        clientRepo.delete(id);
    }

    public void assignToProject(int clientId, int projectId) throws SQLException {
        clientRepo.assignToProject(clientId, projectId);
    }

    public void removeFromProject(int clientId, int projectId) throws SQLException {
        clientRepo.removeFromProject(clientId, projectId);
    }

    // Task 3: Clients linked to any project ending within daysUntilDeadline days from today.
    public List<Client> findClientsByUpcomingProjectDeadline(int daysUntilDeadline) throws SQLException {
        if (daysUntilDeadline < 0)
            throw new IllegalArgumentException("Days until deadline cannot be negative.");
        List<Project> upcoming = projectRepo.findProjectsEndingWithinDays(daysUntilDeadline);
        if (upcoming.isEmpty()) return new ArrayList<>();
        List<Integer> projectIds = new ArrayList<>();
        for (Project p : upcoming) projectIds.add(p.getId());
        return clientRepo.findByProjectIds(projectIds);
    }
}
