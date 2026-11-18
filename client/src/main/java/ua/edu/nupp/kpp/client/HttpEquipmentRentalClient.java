package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpEquipmentRentalClient {
    private static final String BASE = "http://localhost:8080/equipment-rental-server";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Client> getClients() {
        return Arrays.asList(read(get("/clients"), Client[].class));
    }
    public void addClient(Client c) {
        post("/clients", "action=add&fullName=" + enc(c.getFullName()) + "&phone=" + enc(c.getPhone()) + "&email=" + enc(c.getEmail()));
    }
    public void updateClient(Client c) {
        post("/clients", "action=update&id=" + c.getId() + "&fullName=" + enc(c.getFullName()) + "&phone=" + enc(c.getPhone()) + "&email=" + enc(c.getEmail()));
    }
    public void deleteClient(long id) {
        post("/clients", "action=delete&id=" + id);
    }

    public List<Equipment> getEquipmentList() {
        return Arrays.asList(read(get("/equipment"), Equipment[].class));
    }
    public void addEquipment(Equipment e) {
        post("/equipment", "action=add&name=" + enc(e.getName()) + "&category=" + enc(e.getCategory()) + "&rentalRate=" + e.getRentalRate());
    }
    public void updateEquipment(Equipment e) {
        post("/equipment", "action=update&id=" + e.getId() + "&name=" + enc(e.getName()) + "&category=" + enc(e.getCategory()) + "&rentalRate=" + e.getRentalRate());
    }
    public void deleteEquipment(long id) {
        post("/equipment", "action=delete&id=" + id);
    }

    public List<RentalContract> getRentalContracts() {
        return Arrays.asList(read(get("/rental-contracts"), RentalContract[].class));
    }
    public void addRentalContract(RentalContract rc) {
        post("/rental-contracts", contractForm("add", rc));
    }
    public void updateRentalContract(RentalContract rc) {
        post("/rental-contracts", contractForm("update", rc) + "&id=" + rc.getId());
    }
    public void deleteRentalContract(long id) {
        post("/rental-contracts", "action=delete&id=" + id);
    }


    public List<RentalRequest> getRentalRequests() {
        return Arrays.asList(read(get("/rental-requests"), RentalRequest[].class));
    }
    public void addRentalRequest(RentalRequest r) {
        post("/rental-requests", requestForm("add", r));
    }
    public void updateRentalRequest(RentalRequest r) {
        post("/rental-requests", requestForm("update", r) + "&id=" + r.getId());
    }
    public void changeRequestState(long id, RentalRequestState state) {
        post("/rental-requests", "action=transition&id=" + id + "&state=" + state.name());
    }
    public void deleteRentalRequest(long id) {
        post("/rental-requests", "action=delete&id=" + id);
    }


    private String contractForm(String action, RentalContract rc) {
        return "action=" + action +
                "&requestId=" + rc.getRequestId() +
                "&contractDate=" + enc(rc.getContractDate()) +
                "&totalCost=" + rc.getTotalCost();
    }

    private String requestForm(String action, RentalRequest r) {
        return "action=" + action +
                "&clientId=" + r.getClientId() +
                "&equipmentId=" + r.getEquipmentId() +
                "&state=" + (r.getState() != null ? r.getState().name() : "CREATED") +
                "&f1=" + r.getF1() +
                "&f2=" + r.getF2() +
                "&f3=" + r.getF3() +
                "&integralIndex=" + r.getIntegralIndex();
    }


    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path)).GET().build();
        return send(req);
    }

    private String post(String path, String form) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build();
        return send(req);
    }

    private String send(HttpRequest req) {
        try {
            HttpResponse<String> r = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (r.statusCode() >= 400)
                throw new RuntimeException(r.body());
            return r.body();
        } catch (Exception e) {
            throw new RuntimeException("Помилка HTTP: " + e.getMessage(), e);
        }
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Помилка JSON", e);
        }
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}