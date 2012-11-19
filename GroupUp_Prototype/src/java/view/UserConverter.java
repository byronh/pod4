/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.GroupScheduleController;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import model.GroupupUser;

/**
 *
 * @author Shuyi
 */
@FacesConverter(forClass=GroupupUser.class,value="userConverter")
public class UserConverter implements Converter {
    @ManagedProperty(value="#{GroupScheduleController}")
    private GroupScheduleController groupScheduleController;
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value.trim().equals("")) {
            return null;
        } else {
            try {
                for (GroupupUser p : groupScheduleController.getSearchUsers()) {
                    if (p.getId() == Integer.parseInt(value)) {
                        return p;
                    }
                }

            } catch (NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid groupup user"));
            }
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
       if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((GroupupUser) value).getId());
        }
    }
    
}
