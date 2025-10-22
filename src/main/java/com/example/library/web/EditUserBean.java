package com.example.library.web;

import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repo.JdbcUserRepository;
import com.example.library.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class EditUserBean {

    private UserRepository userRepo = new JdbcUserRepository();

    private User user = new User();
    private List<SelectItem> roles;

    @PostConstruct
    public void init() {
        roles = Arrays.stream(Role.values())
                .map(r -> new SelectItem(r, r.name().toLowerCase()))
                .collect(Collectors.toList());

        // Если пришёл параметр id — загружаем существующего пользователя
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        if (id != null && !id.isBlank()) {
            long uid = Long.parseLong(id);
            user = userRepo.findById(uid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found by id:" + uid));
        }
    }

    public String save() {
        try {
            userRepo.save(user);
            flashInfo("User saved");
            // PRG: редирект на список
            return "users.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation error", e.getMessage()));
            return null;
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Save failed", "Please try again"));
            return null;
        }
    }

    private void flashInfo(String msg) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getFlash().put("msg", msg);
    }

    public User getUser() {
        return user;
    }

    public List<SelectItem> getRoles() {
        return roles;
    }
}
