package com.equipe7.getserv.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "_service")
public class ServiceEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
    private String title;
	
	@Column(length = 4096, nullable = true)
    private String description;
	
	@Column(nullable = true)
    private String imageURL;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<TagEntity> tags = new ArrayList<>();
    
	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnoreProperties("service_id")
    private List<RateEntity> rates = new ArrayList<>();
    
    @ManyToOne
	@JsonIgnore
	@JoinColumn(name = "profile_id")
    private ProfileEntity profile; 

    public ServiceEntity(){
    	
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public List<RateEntity> getRates() {
		return rates;
	}

	public void setRates(List<RateEntity> rates) {
		this.rates = rates;
	}

	public ProfileEntity getProfile() {
		return profile;
	}

	public void setProfile(ProfileEntity profile) {
		this.profile = profile;
	}
    
}
