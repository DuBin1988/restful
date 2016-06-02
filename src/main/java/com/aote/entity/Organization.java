package com.aote.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

/**
 * 
 * 组织机构实体
 *
 * @author  LGY
 * @version 1.0
 * @since   2016-06-02
 *
 */
@Entity
@Table(name="t_orgnization")
public class Organization implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 代理主键
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;	
    
    /**
     * 版本号
     */
    @Version
    public Long version;
    
    /**
     * 组织名称
     */
    @NotNull
    public String name;

    @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
                 message="{invalid.email}")
    public String email;

    /**
     * 电话
     */
    @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$",
             message="{invalid.phonenumber}")
    public String mobilephone;
    
    /**
     * 生日
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    @Past
    public Date birthday;   
    
    /**
     * 资金
     */
    public BigDecimal captial;
    
    /**
     * 父组织
     */
    @ManyToOne
    public Organization parent;
    
    /**
     * 子组织
     */
    @OneToMany(cascade=CascadeType.ALL, mappedBy="parent")    
    public List<Organization> branches;
}
