package gob.regionancash.bpm;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.isobit.app.jpa.Permission;
import org.isobit.app.jpa.User;
import org.isobit.util.XUtil;

import gob.regionancash.bpm.jpa.BpmActivity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Transactional
@ApplicationScoped
public class UserService {

    @Inject
    JsonWebToken jwt;
	
	public User getCurrentUser() {
		User user=new User();
		user.setUid(XUtil.intValue(jwt.getClaim("uid")));
		if(jwt.containsClaim("directory"))
			user.setDirectoryId(XUtil.intValue(jwt.getClaim("directory")));
		return user;
	}

	public boolean can(User u, String string) {
		EntityManager em=BpmActivity.getEntityManager();
		for(Permission permission:em.createQuery("SELECT pe FROM Permission pe "
				+ "inner join UserRole ur on ur.PK.rid=pe.role.rid "
				+ "where ur.PK.uid=:uid",Permission.class).setParameter("uid",u.getUid()).getResultList()){
			if(permission.getPerm().contains(string))return true;
		}
		return false;
	}
	
	public Object getRoleList(){
		EntityManager em=BpmActivity.getEntityManager();
		User user=this.getCurrentUser();
		return em.createQuery("SELECT r FROM UserRole ur JOIN Role r ON r.rid=ur.PK.rid WHERE ur.PK.uid=:uid")
				.setParameter("uid",user.getUid())
				.getResultList();
	}

	public Object getPermList(){
		EntityManager em=BpmActivity.getEntityManager();
		User user=this.getCurrentUser();
		return em.createQuery("SELECT r FROM UserRole ur JOIN Role r ON r.rid=ur.PK.rid WHERE ur.PK.uid=:uid")
				.setParameter("uid",user.getUid())
				.getResultList();
	}
	
}
