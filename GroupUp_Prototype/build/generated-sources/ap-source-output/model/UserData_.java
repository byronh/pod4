package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2012-10-13T16:47:14")
@StaticMetamodel(UserData.class)
public class UserData_ { 

    public static volatile SingularAttribute<UserData, Long> id;
    public static volatile ListAttribute<UserData, String> friends;
    public static volatile SingularAttribute<UserData, String> email;
    public static volatile SingularAttribute<UserData, String> name;

}