package gob.regionancash.bpm;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import javax.swing.event.ListSelectionEvent;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.isobit.app.X;
import org.isobit.app.model.User;
import org.isobit.app.service.UserService;
import org.isobit.directory.model.Dependency;
import org.isobit.directory.model.People;
import org.isobit.util.XDate;
import org.isobit.util.XMap;
import org.isobit.util.XUtil;

import gob.regionancash.bpm.model.BpmActivity;
import gob.regionancash.bpm.model.BpmDispatch;
import gob.regionancash.bpm.model.BpmDispatchField;
import gob.regionancash.bpm.model.BpmField;
import gob.regionancash.bpm.model.BpmProcessRun;
import gob.regionancash.bpm.model.BpmRelation;

@Transactional
@ApplicationScoped
public class Service {

	@Inject
	UserService userService;

	@ConfigProperty(name = "isobit.upload-dir") 
	String uploadDir;

	@ConfigProperty(name = "quarkus.http.body.uploads-directory") 
	String uploadsDirectory;

	public Object prepare(Integer processId) {
		BpmProcessRun bpmProcessRun = new BpmProcessRun();
		EntityManager em = BpmActivity.getEntityManager();
		List<BpmActivity> activities = em
				.createQuery("SELECT a FROM BpmActivity a WHERE a.processId=:processId ORDER BY a.id")
				.setParameter("processId", 1).setMaxResults(1).getResultList();
		if (!activities.isEmpty()) {
			BpmActivity activity = activities.get(0);
			activity.setFields(
					em.createQuery("SELECT f FROM BpmField f WHERE f.activityId=:activityId ORDER BY f.weight ASC")
							.setParameter("activityId", activity.getId()).getResultList());
			bpmProcessRun.setActivity(activity);
		}
		bpmProcessRun.setDispatch(new BpmDispatch());
		return bpmProcessRun;
	}

	private Date getDate(Object d) {
		if (d instanceof BpmField)
			d = ((BpmField) d).getValue();
		try {
			return new Date(Long.parseLong(d.toString()));
		} catch (NumberFormatException nfe) {
			try {
				return new SimpleDateFormat("yyyy-MM-dd").parse(d.toString());
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Object postRun(BpmProcessRun entity) {
		EntityManager em = BpmProcessRun.getEntityManager();
		BpmActivity activity = entity.getActivity();
		List peoples = entity.getPeoples();
		User u = userService.getCurrentUser();
		// People people = (People) sessionFacade.get("people");
		BpmDispatch dispatchOld = entity.getDispatch();

		if (entity.getId() == null) {
			entity.setInsertDate(X.getServerDate());
			em.persist(entity);
		} else {
			entity = em.find(BpmProcessRun.class, entity.getId());
			// em.merge(entity);
		}

		List<BpmField> fields = activity.getFields();
		BpmDispatch dispatch = dispatchOld != null && dispatchOld.getId() != null
				? em.find(BpmDispatch.class, dispatchOld.getId())
				: new BpmDispatch();

		dispatch.setComments(dispatchOld.getComments());

		boolean attended = dispatch.getAttentionDate() != null;
		if (!attended || dispatch.getId() == null) {
			dispatch.setActivityId(activity.getId());
			dispatch.setPeopleId(entity.getPeopleId().longValue());
			dispatch.setAttentionDate(new Date());
		}
		// System.out.println("entity.getActivityId()="+entity.getActivityId());
		if (dispatch.getId() == null) {
			dispatch.setDependencyId(activity.getDependencyId());
			dispatch.setPositionId(activity.getPositionId());
			dispatch.setEntityId(entity.getId());
			dispatch.setInsertDate(new Date());
			em.persist(dispatch);
		} else {
			em.merge(dispatch);
		}

		List<BpmDispatchField> dispatchFieldList = new ArrayList();
		// Solo se grabara en caso sea la actividad donde estan los people o se este
		// editando con ext.edit=true
		// cuando se prescribe ya no se cuenta el tiempo
		if (activity.getId() == 40)
			entity.setPrescribed(true);
		if (activity.getId() == 37) {
			// em.createQuery(null)
			for (Object peopleMap : peoples) {
				Map pm = (Map) peopleMap;
				boolean canceled=pm.get("delete")!=null;
				System.out.println(pm);
				int id = XUtil.intValue(pm.get("id"));
				BpmDispatchField dispatchField = id > 0 ? em.find(BpmDispatchField.class, id) : null;
				if (dispatchField == null)
					dispatchField = new BpmDispatchField();
				dispatchField.setDispatchId(dispatch.getId());
				dispatchField.setFieldId(28);
				dispatchField.setCanceled(canceled);
				dispatchField.setValue(X.toText(pm.get("code")));
				if (dispatchField.getId() == null)
					em.persist(dispatchField);
				else
					em.merge(dispatchField);

				id = XUtil.intValue(pm.get("nid"));
				dispatchField = id > 0 ? em.find(BpmDispatchField.class, id) : null;
				if (dispatchField == null)
					dispatchField = new BpmDispatchField();
				dispatchField.setDispatchId(dispatch.getId());
				dispatchField.setFieldId(20);
				dispatchField.setCanceled(canceled);
				dispatchField.setValue(X.toText(pm.get("fullName")));
				if (dispatchField.getId() == null)
					em.persist(dispatchField);
				else
					em.merge(dispatchField);

				id = XUtil.intValue(pm.get("aid"));
				dispatchField = id > 0 ? em.find(BpmDispatchField.class, id) : null;
				if (dispatchField == null)
					dispatchField = new BpmDispatchField();
				dispatchField.setDispatchId(dispatch.getId());
				dispatchField.setFieldId(46);
				dispatchField.setCanceled(canceled);
				dispatchField.setValue(X.toText(pm.get("address")));
				if (dispatchField.getId() == null)
					em.persist(dispatchField);
				else
					em.merge(dispatchField);

				id = XUtil.intValue(pm.get("cid"));
				dispatchField = id > 0 ? em.find(BpmDispatchField.class, id) : null;
				if (dispatchField == null)
					dispatchField = new BpmDispatchField();
				dispatchField.setDispatchId(dispatch.getId());
				dispatchField.setFieldId(58);
				dispatchField.setCanceled(canceled);
				dispatchField.setValue(X.toText(pm.get("position")));
				if (dispatchField.getId() == null)
					em.persist(dispatchField);
				else
					em.merge(dispatchField);
				
			}
		}
		Map<String, BpmField> fieldMap = new HashMap();
		for (BpmField field : fields) {
			fieldMap.put(field.getName(), field);
			BpmDispatchField dispatchField = field.getDispatchFieldId() != null
					? em.find(BpmDispatchField.class, field.getDispatchFieldId())
					: null;
			if (dispatchField == null)
				dispatchField = new BpmDispatchField();

			dispatchField.setDispatchId(dispatch.getId());
			dispatchField.setFieldId(field.getId());
			// Este valor se usa para modificar los valores de una entidad enlazada al run
			String fieldName = (String) field.getName();
			// se pone el valor en la entidad se va

			dispatchField.setName(fieldName);
			Object value = field.getValue();
			if (!XUtil.isEmpty(value)) {
				switch (field.getType()) {
				case "F": {// Archivo
					System.setProperty("java.io.tmpdir", uploadsDirectory);
					Object o = field.getValue();
					if (o instanceof Map) {
						Map vm = (Map) o;
						String tempFileName = (String) vm.get("tempFile");
						try {
							File tempFile = new File(uploadsDirectory,
									tempFileName);
							String fileName = dispatch.getId() + "_" + (String) vm.get("fileName");
							if (fileName.length() > 200)
								throw new RuntimeException(fileName + " es " + fileName.length() + ">200");
							if(tempFile.exists()){
								Files.copy(tempFile.toPath(),
										new File(uploadDir+"\\bpm\\" + fileName).toPath(),
										StandardCopyOption.REPLACE_EXISTING);
								value = fileName;
							}else{
								//try move file remotelly
								throw new RuntimeException("File no found! "+tempFile.getAbsolutePath());
							}
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
					break;
				case "TI": {// fecha
					try {
						value = new SimpleDateFormat("H:m:s").format(new Date(Long.parseLong(value.toString())));
					} catch (Exception e) {
					}
				}
					break;
				case "D": {// fecha

					value = new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(value.toString())));
				}
					break;
				}
				try {
					Method method = entity.getClass().getDeclaredMethod(
							"get" + (fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)));
					Class type = method.getReturnType();
					method = entity.getClass().getDeclaredMethod(
							"set" + (fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)), (Class) type);
					Object args = value;
					if (args != null) {
						if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
							args = Integer.parseInt(args.toString());
						}
					}
					try {
						method.invoke(entity, args);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} catch (NoSuchMethodException e) {

				}
				dispatchField.setValue(value != null ? value.toString() : "");
				if (dispatchField.getId() == null)
					em.persist(dispatchField);
				else
					em.merge(dispatchField);
				dispatchFieldList.add(dispatchField);
			}
		}
		BpmField fechaComision = fieldMap.get("fecha_comision");
		if (fechaComision != null) {
			BpmField determinaFechaComision = fieldMap.get("determina_fecha_comision");
			int year = 1;
			if ("comision_falta".equals(determinaFechaComision.getValue())) {
				year = 3;
			}
			Calendar calendar = Calendar.getInstance();
			Date date = getDate(fechaComision);
			calendar.setTime(date);
			calendar.add(Calendar.YEAR, year);
			entity.setLimitDate(calendar.getTime());
		}
		if (activity.getId() == 37) {
			BpmField f = fieldMap.get("caso_numero");
			if (f != null)
				entity.setNumber(Integer.parseInt(f.getValue().toString()));
			f = fieldMap.get("caso_anio");
			if (f != null)
				entity.setYear(Integer.parseInt(f.getValue().toString()));
			f = fieldMap.get("caso_asunto");
			if (f != null)
				entity.setSubject(f.getValue().toString());
		}
		if (activity.getId() == 43) {
			BpmField fecha_notificacion_resolucion = fieldMap.get("fecha_notificacion_resolucion");
			if (fecha_notificacion_resolucion != null) {
				Calendar calendar = Calendar.getInstance();
				Date date = getDate(fecha_notificacion_resolucion);
				calendar.setTime(date);
				calendar.add(Calendar.YEAR, 1);
				entity.setLimitDate(calendar.getTime());
			}
		}
		if (activity.getId() == 62) {
			BpmField date = fieldMap.get("fecha_notificacion_res_final");
			if (date != null)
				entity.setFinalDate(getDate(date));
		}

		// como solo editan los registros no se crearan mas atenciones
		if (!attended) {
			// tendria q cancelarse toda la ruta si se edita algo q cambie el camino
			List<Object[]> l = em.createQuery(
					"SELECT r,a FROM BpmRelation r JOIN BpmActivity a ON a.id=r.activityToId WHERE r.activityFromId=:activityFromId")
					.setParameter("activityFromId", dispatch.getActivityId()).getResultList();
			// System.out.println("l.size()=" + l.size());
			for (Object[] l2 : l) {

				BpmRelation relation = (BpmRelation) l2[0];
				// Se prepara el mensaje a la siguiente actividad
				activity = (BpmActivity) l2[1];
				String condition = relation.getCondition();
				// System.out.println("condition0=" + condition);
				// Si no existe condicion se enviara el mensaje de actividad siguiente
				boolean send = condition == null || condition.toString().trim().length() == 0;
				if (!send) {
					String[] tc = condition.split(" ");// puede ser $1 ==
					// Se itera en los valores que estan en los campos registrados
					if (tc.length > 2) {
						if (tc[0].startsWith("#")) {
							BpmActivity activity2 = new BpmActivity();
							activity2.setId(-dispatch.getId());
							activity2.setDependencyId(activity.getDependencyId());
							activity2.setPositionId(activity.getPositionId());
							activity = activity2;
							send = true;
						} else
							for (BpmDispatchField dispatchField : dispatchFieldList) {
								if (dispatchField.getName().equals(tc[0])) {
									// System.out.println("condition=" + condition);
									// System.out.println(
									// "dispatchField.getName()=" + dispatchField.getName() + "; tc[0]=" + tc[0]);

									boolean ok = false;
									BpmField f = em.find(BpmField.class, dispatchField.getFieldId());
									if ("N".equals(f.getType())) {
										// esto solo debe funcionar en caso se trate de campos numericos
										double aa = XUtil.doubleValue(dispatchField.getValue());
										double b = XUtil.doubleValue(tc[2]);
										if (tc[1].equals(">")) {
											ok = aa > b;
										} else if (tc[1].equals(">=")) {
											ok = aa >= b;
										} else if (tc[1].equals("==")) {
											ok = aa == b;
										} else if (tc[1].equals("<=")) {
											ok = aa <= b;
										} else if (tc[1].equals("<")) {
											ok = aa < b;
										}
									} else {// cualquier otro sera comparacion si es igual debe tenerse en cuenta las
											// fechas
										// System.out.println("dispatchField.getValue()=" + dispatchField.getValue()
										// + " tc[2]=" + tc[2]);
										ok = tc[2].equals(dispatchField.getValue());
									}
									if (ok) {
										send = true;
										// System.out.println("enviar mensaje " + dispatchField.getName());
									}
									break;
								}
							}
						try {
							Field field = entity.getClass().getDeclaredField(tc[0]);
							field.setAccessible(true);
							Object value = field.get(entity);
							boolean ok = false;
							if (field.getType().isAssignableFrom(String.class)) {
								if (tc[1].equals("==")) {
									ok = ("" + value).equalsIgnoreCase(tc[2]);
								} else if (tc[1].equals("!=")) {
									ok = !("" + value).equalsIgnoreCase(tc[2]);
								}
								// System.out.println("value=" + value);
								/// System.out.println("tc[1]=" + tc[1]);
								// System.out.println("tc[2]=" + tc[2]);
								// System.out.println("ok=" + ok);
							} else {
								double aa = XUtil.doubleValue(value);
								double b = XUtil.doubleValue(tc[2]);
								if (tc[1].equals(">")) {
									ok = aa > b;
								} else if (tc[1].equals(">=")) {
									ok = aa >= b;
								} else if (tc[1].equals("==")) {
									ok = aa == b;
								} else if (tc[1].equals("<=")) {
									ok = aa <= b;
								} else if (tc[1].equals("<")) {
									ok = aa < b;
								}
							}
							if (ok) {
								send = true;
							}
						} catch (NoSuchFieldException ex) {

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				// Si no hay send se debe considerar q se llego al final del proceso
				if (send) {
					//System.out.println("se enviara " + activity.getDescription());
					dispatch = new BpmDispatch();
					dispatch.setEntityId(entity.getId());
					dispatch.setInsertDate(X.getServerDate());
					dispatch.setDependencyId(activity.getDependencyId());
					dispatch.setPositionId(activity.getPositionId());
					dispatch.setActivityId(activity.getId());
					em.persist(dispatch);
					// debe tenerse en cuenta q puede haber varias actividades pendientes
					// se debe escoger la actividad mas avanzada
					entity.setActivityId(activity.getId());
					em.persist(entity);
					if (activity.getId() < 0)
						break;
				}
			}
		}
		if (entity.getId() == null)
			em.persist(entity);
		else
			em.merge(entity);
		return entity;
	}

	public Object getRun(Integer id) {
		EntityManager em = BpmProcessRun.getEntityManager();
		BpmProcessRun entity = em.find(BpmProcessRun.class, id);
		em.detach(entity);
		entity.setActivity(em.find(BpmActivity.class, entity.getActivityId()));
		User u = userService.getCurrentUser();
		People people = u.getDirectoryId() != null ? em.find(People.class, u.getDirectoryId()) : null;
		boolean admin = userService.can(u, "SUPER_ADMIN");

		HashMap ext = new HashMap();
		List positionIdList = new ArrayList();
		List dependencyPositionList = new ArrayList();

		/*
		 * if (people != null) { positionIdList.addAll( em.
		 * createQuery("SELECT pm.positionId FROM ProjectMember pm WHERE pm.peopleId=:peopleId AND pm.projectId=:projectId AND pm.status=TRUE"
		 * ) .setParameter("peopleId", Long.parseLong(people.getCode()))
		 * .setParameter("projectId", project.getId()) .getResultList()); }
		 */
		List<Object[]> dispatchList;

		if (entity.getActivityId() > 0) {
			// esto deberia cargarse segun el dispatch
			BpmActivity a = em.find(BpmActivity.class, entity.getActivityId());
			ext.put("currentActivity", a.getDescription());
			Dependency dependency = em.find(Dependency.class, a.getDependencyId());
			ext.put("currentDependency", dependency.getFullName());
		}
		// Debe tenerse en cuenta alquien que si pueda tener todos los privilegios
		//
//        Integer activityId = p.getActivityId();
		// Si hay muchos run por atender habra problema en caso de un area especifica
//        ato.dependencyId IN )
		if (true || people != null || admin) {

			if (true || admin) {
				// despues de cargar el lugar donde se quedo se revisan los envios hechos
				// el admin puede cargar la actividad q debe ser atendida sin importar q area es
				dispatchList = em.createQuery(
						"SELECT r,a FROM BpmDispatch r LEFT JOIN BpmActivity a ON a.id=r.activityId WHERE r.attentionDate IS NULL AND r.entityId=:entityId")
						.setParameter("entityId", entity.getId()).getResultList();
				//System.out.println("==================");
			} else {
				positionIdList.add(0);
				dependencyPositionList.add("_");
				// revisar los mensajes recibidos al area o cargo del usuario actual
				dispatchList = em.createQuery(
						"SELECT r,a FROM BpmDispatch r JOIN BpmActivity a ON a.id=r.activityId WHERE r.attentionDate IS NULL "
								+ "AND ("
								+ "r.dependencyId IN (SELECT c.dependencyId FROM Contract c WHERE c.peopleId=:peopleId AND c.active=TRUE) "
								+ "OR ((r.dependencyId IS NULL OR r.dependencyId=0 ) AND r.positionId IN :positionId)"
								+ "OR (0=:peopleId AND r.dependencyId,'_',r.positionId IN :dependencyPositionList)"
								+ ") " + "AND r.entityId=:entityId")
						.setParameter("peopleId", people != null ? people.getId() : 0)
						.setParameter("dependencyPositionList", dependencyPositionList)
						.setParameter("positionId", positionIdList).setParameter("entityId", entity.getId())
						.getResultList();
				// System.out.println("dispatchList.size=" + dispatchList.size());
			}
			BpmActivity activity = null;
			// System.out.println("dispatchList.size()=" + dispatchList.size());
			if (!dispatchList.isEmpty()) {
				BpmDispatch dispatch = (BpmDispatch) dispatchList.get(0)[0];
				// System.out.println("dispatch.getActivityId()=" + dispatch.getActivityId());
				if (dispatch.getActivityId() < 0) {
					BpmDispatch dispatch0 = em.find(BpmDispatch.class, -dispatch.getActivityId());
					TypedQuery<Object[]> q = getTypedQuery();
					for (Object[] row : q.setParameter("activityFromId", dispatch0.getActivityId()).getResultList()) {
						BpmRelation relation = (BpmRelation) row[0];
						BpmActivity activity2 = (BpmActivity) row[1];
						//System.out.println("activity == " + activity2);
						// Se prepara el mensaje a la siguiente actividad
						String condition = relation.getCondition();
						// System.out.println("condition=" + condition);
						boolean send = condition == null || condition.toString().trim().length() == 0;
						String[] tc = condition.split(" ");
						em.detach(dispatch);
						if ("#comparacionPAD".equals(tc[0])) {
							BpmActivity activity22 = comparacionPAD(dispatch0, activity2, tc);
							if (activity22 != null) {
								entity.setActivity(activity = activity22);
								entity.setActivityId(activity.getId());
								entity.setDispatch(dispatch);
								dispatch.setActivityId(activity.getId());
							}
						} else if ("#enLimiteDescargo5d".equals(tc[0])) {
							BpmActivity activity22 = enLimiteDescargo5d(dispatch0, activity2, tc);
							if (activity22 != null) {
								entity.setActivity(activity = activity22);
								entity.setActivityId(activity.getId());
								entity.setDispatch(dispatch);
								dispatch.setActivityId(activity.getId());
							}
						}
					}
				} else {
					activity = (BpmActivity) dispatchList.get(0)[1];
					entity.setDispatch(dispatch);
					entity.setActivity(activity);
					// En RunField estan los datos del mensaje
					// Solo sirve si se deseara cargar el mismo dispach para editarlo
				}
				List<BpmDispatchField> dispatchFieldList2 = em
						.createQuery("SELECT rf FROM BpmDispatchField rf WHERE rf.dispatchId=:dispatchId AND rf.canceled=FALSE")
						.setParameter("dispatchId", dispatch.getId()).getResultList();

			} else if (XUtil.intValue(entity.getActivityId()) == 0) {
				// No tiene actividad definida
				// Se asume por ahora que iniciara el proceso
				try {

					// Se debera
					if (admin) {
						activity = (BpmActivity) em
								.createQuery(
										"select a from BpmActivity a where a.id=(SELECT min(a.id) from BpmActivity a)")
								.getSingleResult();
					} else {
						activity = (BpmActivity) em.createQuery(
								"select a from BpmActivity a where a.id=(SELECT min(a.id) from BpmActivity a where a.dependencyId IN (SELECT c.dependencyId FROM Contract c WHERE c.peopleId=:people AND c.active=TRUE))")
								.setParameter("people", people.getId()).getSingleResult();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// System.out.println("" + project.getId() + " activity=" +
				// project.getActivityId());
				// Si la actividad actual es menor que cero se debe iniciar el proceso
				throw new RuntimeException("Usted no tiene ninguna actividad por atender");

			}

			Date limit = entity.getLimitDate();
			if (limit != null && !entity.isPrescribed()) {
				Date today = new Date();
				Period period = Period.between(limit.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
						today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				int ye = period.getYears() * 10000 + period.getMonths() * 100 + period.getDays();
				if (ye > 0) {
					BpmActivity BpmActivity = new BpmActivity();
					activity = em.find(BpmActivity.class, 40);
					BpmActivity.setId(activity.getId());
					BpmActivity.setDescription(activity.getDescription());
					entity.setActivity(activity = BpmActivity);
					entity.setActivityId(activity.getId());
				}
				activity.setProgress(ye > 0 ? 101 : 0);
				activity.setMsg((ye + " " + sdf.format(limit) + ">" + sdf.format(limit) + "|" + period.getDays() + "/"
						+ period.getMonths() + "/" + period.getYears()));
			}

			if (activity != null) {
				// Se cargaran todos los campos disponibles para el formulario de la actividad
				List<BpmField> fieldActivityList = em
						.createQuery("SELECT f FROM BpmField f WHERE f.activityId=:activity ORDER  BY f.weight ASC")
						.setParameter("activity", activity.getId()).getResultList();
				for (BpmField bpmField : fieldActivityList) {
					String fieldName = bpmField.getName();
					// System.out.println("fieldName=" + fieldName);
					try {
						Method method = entity.getClass().getDeclaredMethod(
								"get" + (fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)));
						try {
							

							
							if(bpmField.getId()==47){
								bpmField.setValue(method.invoke(entity));
							}else{
								bpmField.setValue(method.invoke(entity));
							}
							// System.out.println("value=" + bpmField.getValue());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					} catch (NoSuchMethodException e) {
					}
				}
				activity.setFields(fieldActivityList);
				entity.setActivity(activity);
			} else {
				throw new RuntimeException("Usted no tiene ninguna actividad por atender");
			}
		}

		return entity;
	}

	public List getDetails(Integer id) {
		// System.out.println("getDetails");
		EntityManager em = BpmProcessRun.getEntityManager();
		TypedQuery<Object[]> q = getTypedQuery();
		List<Object[]> details = em
				.createQuery("SELECT r," + "ato.description," + "p.fullName," + "CONCAT(t.name,' ',de.name) "
						+ ",CONCAT(f.type,'.',f.name,'.',f.label,'=', rf.value)," + "po.name," + "0 "
						+ "FROM BpmDispatch r \n"
						+ "LEFT JOIN BpmActivity ato ON ato.id=r.activityId LEFT JOIN People p ON p.id=r.peopleId \n"
						+ "LEFT JOIN Dependency de ON de.id=r.dependencyId LEFT JOIN de.type t \n"
						+ "LEFT JOIN Position po ON po.id=r.positionId  \n"
						+ "LEFT JOIN BpmDispatchField rf ON rf.dispatchId=r.id AND rf.canceled=FALSE \n"
						+ "LEFT JOIN BpmField f ON f.id=rf.fieldId WHERE r.entityId=:entity ORDER BY r.id \n")
				.setParameter("entity", id).getResultList();
		List details2 = new ArrayList();
		Object last = false;
		List tempList = null;
		Object[] rowt = null;
		for (Object[] r : details) {
			BpmDispatch dispatch = (BpmDispatch) r[0];
			if (dispatch.getActivityId() < 0) {
				BpmDispatch dispatch0 = em.find(BpmDispatch.class, -dispatch.getActivityId());
				for (Object[] row : q.setParameter("activityFromId", dispatch0.getActivityId()).getResultList()) {
					BpmRelation relation = (BpmRelation) row[0];
					BpmActivity activity = (BpmActivity) row[1];

					// Se prepara el mensaje a la siguiente actividad
					String condition = relation.getCondition();
					boolean send = condition == null || condition.toString().trim().length() == 0;
					String[] tc = condition.split(" ");

					if ("#comparacionPAD".equals(tc[0])) {
						BpmActivity activity2 = comparacionPAD(dispatch0, activity, tc);
						if (activity2 != null) {
							r[1] = activity2;
						}
					} else if ("#enLimiteDescargo5d".equals(tc[0])) {
						BpmActivity activity2 = enLimiteDescargo5d(dispatch0, activity, tc);
						if (activity2 != null)
							r[1] = activity2;
					}
				}
			}
			Long popleId = dispatch.getPeopleId();
			if (popleId != null) {
				if (popleId < 0) {
					User u = em.find(User.class, -popleId.intValue());
					if (u != null)
						r[2] = u.getName();
				}
			}
			if (!last.equals(dispatch.getId())) {
				details2.add(rowt = r);
				last = dispatch.getId();
			}
			if (r[4] != null) {
				if (!(rowt[4] instanceof List)) {
					List l = new ArrayList();
					l.add(r[4]);
					rowt[4] = l;
				} else
					((List) rowt[4]).add(r[4]);
			}
		}
		return details2;
	}

	public Object getUserList() {
		EntityManager em = BpmField.getEntityManager();
		return em
				.createQuery(
						"SELECT u FROM UserRole ur INNER JOIN User u ON u.uid=ur.PK.uid "
								+ "	WHERE ur.PK.rid=53 AND (u.name LIKE 'pad-%' OR u.name LIKE 'abogado-%') ORDEr BY u.name",
						User.class)
				.getResultList().stream().map((o) -> {
					Map m = new HashMap();
					m.put("uid", o.getUid());
					m.put("name", o.getName());
					return m;
				}).collect(Collectors.toList());
	}

	public static int getDiffYears(Date first, Date last) {
		Calendar a = getCalendar(first);
		Calendar b = getCalendar(last);
		int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
		if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
				|| (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
			diff--;
		}
		return diff;
	}

	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.setTime(date);
		return cal;
	}

	private static SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public static void main(String args[]) {
		LocalDate firstDate = LocalDate.of(2015, 1, 1);
		LocalDate secondDate = LocalDate.of(2018, 3, 4);

		Period period = Period.between(firstDate, secondDate);
		// System.out.print(period.getYears());

		/*
		 * LocalDate startLocalDate = startDate.toInstant()
		 * .atZone(ZoneId.systemDefault()) .toLocalDate();
		 */
	}

	public Object[] getT(Date from, int years) {
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(from);
		calendar.add(Calendar.YEAR, years);
		double diffInMillies = calendar.getTimeInMillis() - from.getTime();
		diffInMillies = (100.0 * (today.getTime() - from.getTime()) / (1.0 * diffInMillies));
		int diff[] = null;
		Period period = Period.between(calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		int ye = period.getYears() * 10000 + period.getMonths() * 100 + period.getDays();
		return new Object[] { diffInMillies, (sdf.format(from) + ">" + sdf.format(calendar.getTime()) + "|"
				+ period.getDays() + "/" + period.getMonths() + "/" + period.getYears()), ye <= 0 };
	}

	private Date parseDate(String date) {
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			try {
				return sdf0.parse(date);
			} catch (ParseException e2) {
				throw new RuntimeException(e2);
			}
		}
	}

	public BpmActivity comparacionPAD(BpmDispatch dispatch2, BpmActivity activity, String tc[]) {
		EntityManager em = BpmProcessRun.getEntityManager();
		Date fecha_comision_falta = new Date(), fecha_subgerencia_SGRH = fecha_comision_falta,
				fecha_informe_control = fecha_comision_falta;
		String determina_fecha_comision = null;
		String msg = "";
		// System.out.println("inicia " + tc[0] + " " + tc[1] + " " + tc[2]);
		boolean b = false;
		Date today = new Date();
		for (Object[] e : em.createQuery(
				"SELECT rf,fi FROM BpmDispatchField rf INNER JOIN BpmField fi ON fi.id=rf.fieldId WHERE rf.dispatchId=:dispatchId AND rf.canceled=0 ",
				Object[].class).setParameter("dispatchId", dispatch2.getId()).getResultList()) {
			BpmDispatchField df = (BpmDispatchField) e[0];
			BpmField fi = (BpmField) e[1];
			try {
				if ("determina_fecha_comision".equals(fi.getName())) {
					determina_fecha_comision = df.getValue();
					// System.out.println("determina_fecha_comision=" + determina_fecha_comision);
				}
				if ("comision_falta".equals(determina_fecha_comision)) {
					fecha_comision_falta = df.getValue() != null ? parseDate(df.getValue().toString()) : new Date();
					Object res[] = getT(fecha_comision_falta, 3);
					activity.setProgress((Number) res[0]);
					activity.setMsg((String) res[1]);
					//System.out.println(sdf.format(fecha_comision_falta) + "  " + sdf.format(today));
					b = (boolean) res[2]; // getDiffYears(fecha_comision_falta, today) >= 3;
				} else if ("subgerencia_SGRH".equals(determina_fecha_comision)) {
					fecha_subgerencia_SGRH = df.getValue() != null ? parseDate(df.getValue().toString()) : new Date();
					Object res[] = getT(fecha_subgerencia_SGRH, 1);
					activity.setProgress((Number) res[0]);
					activity.setMsg((String) res[1]);
					b = (boolean) res[2];// getDiffYears(fecha_subgerencia_SGRH, today) >= 1;
				} else if ("informe_control".equals(determina_fecha_comision)) {
					fecha_informe_control = df.getValue() != null ? parseDate(df.getValue().toString()) : new Date();
					Object res[] = getT(fecha_informe_control, 1);
					activity.setProgress((Number) res[0]);
					activity.setMsg((String) res[1]);
					b = (boolean) res[2];// getDiffYears(fecha_informe_control, today) >= 1;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		int bb = b ? 0 : 1;
		if (Integer.parseInt(tc[2]) == bb) {
			//System.out.println("retorna " + activity);
			return activity;
		}
		return null;
	}

	public BpmActivity enLimiteDescargo5d(BpmDispatch dispatch2, BpmActivity activity, String tc[]) {
		int b = TimeUnit.DAYS.convert(Math.abs(new Date().getTime() - dispatch2.getInsertDate().getTime()),
				TimeUnit.MILLISECONDS) <= 5 ? 1 : 0;
		if (Integer.parseInt(tc[2]) == b) {
			return activity;
		} else if (Integer.parseInt(tc[2]) == b)
			return activity;
		return null;
	}

	public TypedQuery<Object[]> getTypedQuery() {
		EntityManager em = BpmProcessRun.getEntityManager();
		return em.createQuery(
				"SELECT re,ac FROM BpmRelation re INNER JOIN BpmActivity ac ON ac.id=re.activityToId WHERE re.activityFromId=:activityFromId",
				Object[].class);
	}

	public Object load(Integer first, Integer pageSize, Object object, HashMap filters) {
		EntityManager em = BpmProcessRun.getEntityManager();
		Object year = filters.get("year");
		Object number = filters.get("number");
		Object subject = filters.get("subject");
		TypedQuery<Object[]> q, q2 = getTypedQuery();
		List<Query> ql = new ArrayList();
		String pre, post;
		Integer delegatedUserId = null;
		User u = userService.getCurrentUser();
		//System.out.println("u=" + u);
		boolean pad_admin = userService.can(u, "PAD_ADMIN");

		// delegatedUserId
		ql.add(q = em.createQuery(
				"SELECT ru,di,GROUP_CONCAT(CONCAT(fi.name,'=',df.value),'|') " + (pre = " FROM BpmProcessRun ru ")
				// Se intenta cargar los campos iniciales del proceso
						+ ("LEFT JOIN BpmDispatch di0 ON di0.entityId=ru.id AND di0.activityId=37 "
								+ "LEFT JOIN BpmDispatchField df ON df.dispatchId=di0.id AND df.canceled=FALSE "
								+ "LEFT JOIN BpmField fi ON fi.id=df.fieldId ")

						// agrega el ultimo dispatch enviado a un destino
						+ (post = (" LEFT JOIN BpmDispatch di ON di.entityId=ru.id AND di.attentionDate IS NULL "
								+ "WHERE ru.canceled=FALSE "
								+ (pad_admin ? ""
										: " AND (ru.delegatedUserId IS NULL OR ru.delegatedUserId=:delegatedUser)")
								+ (year != null ? " AND ru.year=:year " : "")
								+ (delegatedUserId != null ? " AND ru.delegatedUserId=:delegatedUserId " : "")
								+ (number != null ? " AND ru.number=:number " : "")
								+ (subject != null ? " AND UPPER(ru.subject) LIKE UPPER(:subject) " : "")))
						+ "GROUP BY ru,di ORDER BY ru.id DESC",
				Object[].class));
		if (pageSize > 0) {
			ql.get(0).setFirstResult(first).setMaxResults(pageSize);
			ql.add(em.createQuery("SELECT COUNT(ru)" + pre + post));
		}
		for (Query qq : ql) {
			if (year != null) {
				qq.setParameter("year", year);
			}
			if (!pad_admin) {
				qq.setParameter("delegatedUser", u.getUid());

			}
			if (number != null) {
				qq.setParameter("number", number);
			}
			if (delegatedUserId != null) {
				qq.setParameter("delegatedUserId", delegatedUserId);
			}
			if (subject != null) {
				//System.out.println("%" + subject + "%");
				qq.setParameter("subject", "%" + subject + "%");
			}
		}
		if (pageSize > 0) {
			filters.put("size", ql.get(1).getSingleResult());
		}
		return ((TypedQuery<Object[]>) ql.get(0)).getResultList().stream().map((o) -> {
			BpmProcessRun run = (BpmProcessRun) o[0];
			BpmDispatch dispatch = (BpmDispatch) o[1];
			if (dispatch != null) {
				if (dispatch.getActivityId() > 0)
					run.setActivity(em.find(BpmActivity.class, dispatch.getActivityId()));
				else {
					BpmDispatch dispatch0 = em.find(BpmDispatch.class, -dispatch.getActivityId());

					for (Object[] row : q2.setParameter("activityFromId", dispatch0.getActivityId()).getResultList()) {
						BpmRelation relation = (BpmRelation) row[0];
						BpmActivity activity = (BpmActivity) row[1];
						// Se prepara el mensaje a la siguiente actividad
						String condition = relation.getCondition();
						boolean send = condition == null || condition.toString().trim().length() == 0;
						String[] tc = condition.split(" ");

						if ("#comparacionPAD".equals(tc[0])) {
							//System.out.println("buscando para ====== " + dispatch0.getId());
							BpmActivity activity2 = comparacionPAD(dispatch0, activity, tc);
							if (activity2 != null) {
								run.setActivity(activity2.clone());
								// System.out.println(dispatch0.getId() + " - " + "msg=" + activity2.getMsg());
							}
						} else if ("#enLimiteDescargo5d".equals(tc[0])) {
							BpmActivity activity2 = enLimiteDescargo5d(dispatch0, activity, tc);
							if (activity2 != null)
								run.setActivity(activity2.clone());
						}
					}
					// Se realiza una busqueda en los posibles casos en base a los parametros
				}
				run.setDispatch(dispatch);
			} else {
				BpmDispatch dispatch2 = new BpmDispatch();
				BpmActivity activity2 = new BpmActivity();
				activity2.setDescription("PROCESO TERMINADO");
				activity2.setMsg("");
				run.setActivity(activity2);
				run.setDispatch(dispatch2);
			}
			BpmActivity activity = run.getActivity();

			// Ya se entrego la resolucion final
			if (run.getFinalDate() != null) {
				// Si aun hay tiempo de apelacion se muestra la cuenta regresiva en amarillo
				// pero ya no se cambiara su camino
				// Siempre sera amarillo hasta q pase el tiempo de apelacion
				Date fin = run.getFinalDate();

				Date today = new Date();
				// Se calculara la fecha despues de 15 dias laborales
				// se deducira las fechas de calendario q son libres
				// Se deducira los sabados y domingos
				// es mejor calcular la fecha limite de apelacion al momento de grabar la fecha
				// inicial and every time dates change recalculate

				Calendar c = Calendar.getInstance();
				c.setTime(fin);
				int da = 0;
				int days = 0;
				while (da < 15) {
					c.add(Calendar.DAY_OF_MONTH, 1);
					int dow = c.get(Calendar.DAY_OF_WEEK);
					if ((dow >= Calendar.MONDAY) && (dow <= Calendar.FRIDAY)) {
						// Check is date is not in holidays
						da++;
					}
					days++;
				}
				Date impugnLimitDate = c.getTime();
				c.add(Calendar.DAY_OF_WEEK, -(days - da));
				Period period = Period.between(c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
						today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

				int ye = period.getYears() * 10000 + period.getMonths() * 100 + period.getDays();
				activity.setProgress(ye > 0 ? 101 : 0);
				activity.setMsg((ye + " " + sdf.format(fin) + ">" + sdf.format(impugnLimitDate) + "|" + period.getDays()
						+ "/" + period.getMonths() + "/" + period.getYears()));
			} else if (activity != null && !run.isPrescribed()) {
				Date limit = run.getLimitDate();
				if (limit != null) {
					Date today = new Date();
					Period period = Period.between(limit.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
							today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
					int ye = period.getYears() * 10000 + period.getMonths() * 100 + period.getDays();
					if (ye > 0) {
						BpmActivity BpmActivity = new BpmActivity();
						activity = em.find(BpmActivity.class, 40);
						BpmActivity.setId(activity.getId());
						BpmActivity.setDescription(activity.getDescription());
						run.setActivity(activity = BpmActivity);
						run.setActivityId(activity.getId());
					}
					activity.setProgress(ye > 0 ? 101 : 0);
					activity.setMsg((ye + " " + sdf.format(limit) + ">" + sdf.format(limit) + "|" + period.getDays()
							+ "/" + period.getMonths() + "/" + period.getYears()));
				}
			}
			String extString = (String) o[2];
			if (extString != null) {
				for (String item : extString.split("|,")) {

				}
				run.setExt(o[2]);
			}
			if (run.getDelegatedUserId() != null) {
				User user = em.find(User.class, run.getDelegatedUserId());
				if (user != null)
					run.setDelegatedUser(user.getName());
			}
			if (activity != null && activity.getId() == null)
				activity.setMsg("");
			return run;
		}).collect(Collectors.toList());
	}

	public Object deleteRun(Integer id) {
		BpmProcessRun run = BpmProcessRun.findById(id);
		run.setCanceled(true);
		run.persist();
		return true;
	}

	public Object getDispatch(Integer dispatchId) {
		EntityManager em = BpmProcessRun.getEntityManager();
		BpmDispatch dispatch = em.find(BpmDispatch.class, dispatchId);
		BpmProcessRun entity = em.find(BpmProcessRun.class, dispatch.getEntityId());
		em.detach(entity);
		entity.setDispatch(dispatch);
		entity.setActivity(em.find(BpmActivity.class, dispatch.getActivityId()));
		User u = userService.getCurrentUser();

		HashMap ext = new HashMap();

		List<Object[]> dispatchList;
		BpmActivity activity = em.find(BpmActivity.class, dispatch.getActivityId());
		if (dispatch.getActivityId() > 0) {
			ext.put("currentActivity", activity.getDescription());
			Dependency dependency = em.find(Dependency.class, activity.getDependencyId());
			ext.put("currentDependency", dependency.getFullName());
		}
		List<Object[]> fieldActivityList = em.createQuery("SELECT f,df FROM BpmField f "
				+ "LEFT JOIN BpmDispatchField df ON df.dispatchId=:dispatchId AND f.id=df.fieldId WHERE df.canceled=FALSE AND f.activityId=:activity ORDER  BY f.weight ASC")
				.setParameter("dispatchId", dispatch.getId()).setParameter("activity", activity.getId())
				.getResultList();
		List<BpmField> ll = new ArrayList();
		for (Object[] row : fieldActivityList) {
			BpmField bpmField = (BpmField) row[0];
			bpmField = (BpmField) bpmField.clone();
			BpmDispatchField dispatchField = (BpmDispatchField) row[1];
			if (dispatchField != null) {
				bpmField.setValue(dispatchField.getValue());
				bpmField.setDispatchFieldId(dispatchField.getId());
				// Aqui debe de listarse todos los datos de la persona asi s la primera
				// actividfad

			}
			String fieldName = bpmField.getName();
			if (bpmField.getId() == 28) {
				//el campo especial del campo tipo tabla del 
				
				bpmField.setValue(em.createQuery("SELECT o FROM Offender o").getResultList());
			}


			try {
				Method method = entity.getClass()
						.getDeclaredMethod("get" + (fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)));
				try {
					bpmField.setValue(method.invoke(entity));
					// System.out.println("value=" + bpmField.getValue());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} catch (NoSuchMethodException e) {
			}
			ll.add(bpmField);
		}
		activity.setFields(ll);

		entity.setActivity(activity);
		return entity;
	}

	public Object loadRelation(Integer first, Integer pageSize, Object object, HashMap filters) {
		EntityManager em = BpmProcessRun.getEntityManager();
		List<Query> ql = new ArrayList();
		String sql;
		ql.add(em.createQuery("SELECT r,tf.name,df.name,acf,tt.name,dt.name,act "
				+ (sql = " FROM BpmRelation r LEFT JOIN BpmActivity acf ON acf.id=r.activityFromId LEFT JOIN Dependency df ON df.id=acf.dependencyId LEFT JOIN df.type tf LEFT JOIN BpmActivity act ON act.id=r.activityToId LEFT JOIN Dependency dt ON dt.id=act.dependencyId LEFT JOIN dt.type tt ORDER BY r.activityFromId")));
		if (pageSize > 0) {
			ql.get(0).setFirstResult(first).setMaxResults(pageSize);
			ql.add(em.createQuery("SELECT COUNT(r) " + sql));
		}
		if (pageSize > 0) {
			filters.put("size", ql.get(1).getSingleResult());
		}
		return ((TypedQuery<Object[]>) ql.get(0)).getResultList().stream().map((row) -> {
			BpmRelation o = (BpmRelation) row[0];
			row[0] = null;
			o.setExt(new XMap("dependencyFrom", row[1] + " " + row[2], "activityFrom", row[3], "dependencyTo",
					row[4] + " " + row[5], "activityTo", row[6]));
			return o;
		}).collect(Collectors.toList());
	}

}
