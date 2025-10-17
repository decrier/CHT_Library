package com.example.library.web;

import com.example.library.model.User;
import com.example.library.repo.JdbcUserRepository;
import com.example.library.repo.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class AddUserBean {
    private String name;
    private String email;
    private UserRepository repo = new JdbcUserRepository();

    public String add() {
        try {
            if (name == null || name.isBlank() || email == null || email.isBlank()) {
                FacesContext.getCurrentInstance().addMessage( null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Name and email are required", null));
                return null;
            }
            repo.save(new User(name,email));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User added", null));
            return "/users.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException e ){
            FacesContext.getCurrentInstance().addMessage( null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null;
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "DB error: " + e.getMessage(), null));
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
