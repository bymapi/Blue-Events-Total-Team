// package com.example.security.entities;


// import java.io.Serializable;
// import java.util.List;

// import jakarta.persistence.CascadeType;
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.JoinTable;
// import jakarta.persistence.ManyToMany;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
 
 
// @Entity
// @Table(name = "usuarios")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
 
// public class MyUser implements Serializable {
 
//     private static final long serialVersionUID =1L;
 
//     @Column(name = "idUsuario")
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private int idUsuario;

//     @Column(name = "username")
//     private String userName;
    
//     private String password;
 
//     @ManyToMany(fetch = FetchType.EAGER,
//      cascade = {CascadeType.PERSIST,
//          CascadeType.MERGE})
   
//     @JoinTable(name = "usuarios_roles",
//         joinColumns = { @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario") },
//         inverseJoinColumns = { @JoinColumn(name = "id_role", referencedColumnName = "id_role") })
 
//         private List<Role> roles;
//      }