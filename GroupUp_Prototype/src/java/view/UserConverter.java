/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javax.faces.application.FacesMessage;
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

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {   GroupupUser user = new GroupupUser();   user.setFname("Shuyi"); return user;
       /* if (value.trim().equals("")) {
            return null;
        } else {
            try {
                // find which player matches the value in database

            } catch (NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"));
            }
        }

        return null;*/
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
       if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((GroupupUser) value).getFname());
        }
    }
    
}
