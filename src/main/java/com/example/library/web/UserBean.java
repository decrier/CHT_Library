package com.example.library.web;

import com.example.library.model.User;
import com.example.library.repo.JdbcUserRepository;
import com.example.library.repo.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.util.List;

@Named("userBean")
@RequestScoped
public class UserBean {
    private final UserRepository repo = new JdbcUserRepository();

    public List<User> getUsers() {
        return repo.findAll();
    }

    public String delete(long id) {
        boolean removed = repo.deleteById(id);
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
}
