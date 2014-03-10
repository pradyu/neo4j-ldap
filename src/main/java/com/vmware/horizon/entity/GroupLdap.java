package com.vmware.horizon.entity;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.util.Set;

@Entry(objectClasses = {"group"})
public class GroupLdap extends LdapObject {

    @Id
    private Name dn;

    @Attribute(name="cn")
    @DnAttribute(value = "cn", index = 1)
    private String name;

    @Attribute(name="memberOf")
    private Set<String> memberOf;


    @Attribute(name="member")
    private Set<String> member;

    @Attribute(name = "objectGUID")
    private String objectGUID;

    private String canonicalName;


    public Name getDn() {
        return dn;
    }

    public void setDn(Name dn) {
        this.dn = dn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(Set<String> memberOf) {
        this.memberOf = memberOf;
    }

    public Set<String> getMember() {
        return member;
    }

    public void setMember(Set<String> member) {
        this.member = member;
    }

    public String getObjectGUID() {
        return objectGUID;
    }

    public void setObjectGUID(String objectGUID) {
        this.objectGUID = objectGUID;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
}
