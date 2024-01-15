package org.example.Entities.OneToManyToOne_Bidirectional;

import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

public class CategoryInterceptor implements Interceptor {
    /**
     * Called before an object is saved. The interceptor may modify the {@code state}, which will be used for
     * the SQL {@code INSERT} and propagated to the persistent object.
     *
     * @param entity        The entity instance whose state is being inserted
     * @param id            The identifier of the entity
     * @param state         The state of the entity which will be inserted
     * @param propertyNames The names of the entity properties.
     * @param types         The types of the entity properties
     * @return {@code true} if the user modified the {@code state} in any way.
     * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
     */
    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof CategoryEntity) {
            CategoryEntity category = (CategoryEntity) entity;
            System.out.println(category.toString() + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        }

        return Interceptor.super.onSave(entity, id, state, propertyNames, types);
    }

    /**
     * Called before an object is deleted. It is not recommended that the interceptor modify the {@code state}.
     *
     * @param entity        The entity instance being deleted
     * @param id            The identifier of the entity
     * @param state         The state of the entity
     * @param propertyNames The names of the entity properties.
     * @param types         The types of the entity properties
     * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
     */
    @Override
    public void onDelete(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof CategoryEntity) {
            CategoryEntity category = (CategoryEntity) entity;
            System.out.println(category.toString() + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        }

        Interceptor.super.onDelete(entity, id, state, propertyNames, types);
    }

    //................
}
