package controllers;

import controllers.CRUD.ObjectType;
import play.Play;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;


public abstract class TlCrud extends CRUD {
	
	public static int getPageSize() {
        return Integer.parseInt(Play.configuration.getProperty("crud.pageSize", "30"));
    }
	
}
