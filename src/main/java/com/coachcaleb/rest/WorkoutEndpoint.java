package com.coachcaleb.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import com.coachcaleb.model.Workout;

/**
 * 
 */
@Stateless
@Path("/workouts")
public class WorkoutEndpoint
{
   @PersistenceContext(unitName = "forge-default")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(Workout entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(WorkoutEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Workout entity = em.find(Workout.class, id);
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      em.remove(entity);
      return Response.noContent().build();
   }

   @GET
   @Path("/{id:[0-9][0-9]*}")
   @Produces("application/json")
   public Response findById(@PathParam("id") Long id)
   {
      TypedQuery<Workout> findByIdQuery = em.createQuery("SELECT DISTINCT w FROM Workout w WHERE w.id = :entityId ORDER BY w.id", Workout.class);
      findByIdQuery.setParameter("entityId", id);
      Workout entity;
      try
      {
         entity = findByIdQuery.getSingleResult();
      }
      catch (NoResultException nre)
      {
         entity = null;
      }
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/json")
   public List<Workout> listAll()
   {
      final List<Workout> results = em.createQuery("SELECT DISTINCT w FROM Workout w ORDER BY w.id", Workout.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(Workout entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}