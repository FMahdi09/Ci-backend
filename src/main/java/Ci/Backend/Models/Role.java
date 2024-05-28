package Ci.Backend.Models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "roles")
public class Role
{
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String name;

    public Role(String name)
    {
        this.name = name;
    }

    public Role()
    {
    }

    public GrantedAuthority getAuthority()
    {
        return new SimpleGrantedAuthority("ROLE_" + name);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
