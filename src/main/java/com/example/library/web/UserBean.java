package com.example.library.web;

import com.example.library.model.User;
import com.example.library.repo.JdbcLoanRepository;
import com.example.library.repo.JdbcUserRepository;
import com.example.library.repo.LoanRepository;
import com.example.library.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.util.List;

@Named("userBean")
@RequestScoped
public class UserBean {
    private final UserRepository userRepo = new JdbcUserRepository();
    private final LoanRepository loanRepo = new JdbcLoanRepository();

    private String statusFilter = "ACTIVE";
    private List<User> items;

    @PostConstruct
    public void init() {
        reload();
    }

    private void reload() {
        switch (statusFilter) {
            case "ACTIVE" -> items = userRepo.findAllActiveUsers();
            case "INACTIVE" -> items = userRepo.findAllInactiveUsers();
            case "ALL" -> items = userRepo.findAllAnyStatus();
            default -> {}
        }
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public String delete(long id) {
        boolean removed = userRepo.deleteById(id);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (removed) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "User deleted: id=" + id, null));
        } else {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "User not found: id:" + id, null));
        }
        return "/users.xhtml?faces-redirect=true";
    }

    public String deactivate(long userId) {
        try {
            if (loanRepo.hasActiveLoansByUser(userId)) {
                addError("Нельзя деактивировать: у пользователя есть активные выдачи книг");
                return null;
            }
            if (userRepo.deactivateById(userId)) {
                addInfo("Пользователь деактивирован");
                return "users.xhtml?faces-redirect=true";
            } else {
                addError("Пользователь не найден");
                return null;
            }
        } catch (Exception e) {
            addError("Ошибка деактивации: " + e.getMessage());
            return null;
        }
    }

    public String activate(long userId) {
        try {
            if(userRepo.activateById(userId)) {
                addInfo("Пользователь активирован");
                return "users.xhtml?faces-redirect=true";
            } else {
                addError("Пользователь не найден");
                return null;
            }
        } catch (Exception e) {
            addError("Ошибка активации: " + e.getMessage());
            return null;
        }
    }

    private void addInfo(String msg) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.getFlash().put("msg", msg);
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage( null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg));
    }
}
