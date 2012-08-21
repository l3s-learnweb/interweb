package de.l3s.interwebj.connector.facebook;

import java.util.List;

import l3s.facebook.objects.page.Page;
import l3s.facebook.objects.user.Education;
import l3s.facebook.objects.user.FavoriteAthletes;
import l3s.facebook.objects.user.FavoriteTeams;
import l3s.facebook.objects.user.Languages;
import l3s.facebook.objects.user.Projects;
import l3s.facebook.objects.user.Sports;
import l3s.facebook.objects.user.User;
import l3s.facebook.objects.user.Work;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class PersonalityThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	User friend;
    public PersonalityThread(String str) {
	super(str);
    }
    public PersonalityThread(String threadname, String userid, Facebook fbapi, IndexWriter writer)
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.friend= fbapi.getEntity(userid, User.class);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {
	
    	Document doc= new Document();
		Field field= new Field("id", userid, Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(field);
		field= new Field("name", friend.getName(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("gender", friend.getGender(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("link", friend.getLink(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("locale", friend.getLocale(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("birthday", friend.getBirthday(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("political",friend.getPolitical() , Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("relationship status", friend.getRelationshipStatus(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("updated time", friend.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(field);
		
			
		int i=1;
		for(Education edu: friend.getEducation())
		{
			field= new Field("education"+i+" type", edu.getType(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+" school", edu.getSchool().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+" concentration", edu.getConcentration().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+" year", edu.getYear().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			Page schoolpage = fbapi.getEntity(edu.getSchool().getId().toString(), Page.class);
			
			field= new Field("education"+i+" school descreption", schoolpage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+" school about", schoolpage.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+"school link", schoolpage.getLink(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+"school id", schoolpage.getId().toString(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			i++;
		}
		i=1;
		for(Work work:friend.getWork())
		{
			field= new Field("work"+i+" location", work.getLocation().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" position", work.getPosition().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" employer", work.getEmployer().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" start date", work.getStartDate(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" end date", work.getEndDate(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" from", work.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			int j=1;
			for(Projects project: work.getProjects())
			{
				field= new Field("work"+i+" project"+j+" name", project.getName(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("work"+i+" project"+j+" description", project.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
			}
			
			Page workpage = fbapi.getEntity(work.getEmployer().getId().toString(), Page.class);
			
			field= new Field("work"+i+" employer descreption", workpage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" employer about", workpage.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+"employer link", workpage.getLink(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			i++;
		}
		List<Languages> languages = friend.getLanguages();
		String languagelist="";
			for(Languages lang: languages)
			{
				languagelist+=lang.getName()+" ";
			}
		field= new Field("languages", languagelist, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);	
		
		String athletes="";
		for(FavoriteAthletes athlete :friend.getFavoriteAthletes())
		{
			athletes+= athlete.getName()+ " ";
		}	
		String teams="";
		for(FavoriteTeams team:friend.getFavoriteTeams())
		{
			teams+=team.getName()+" ";
		}
		String sports= "";
		for(Sports sport:friend.getSports())
		{
			sports+=sport.getName()+" ";
		}
		
		field= new Field("sports", sports, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("favourite athletes", athletes, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("favourite teams", teams, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("bio", friend.getBio(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("website", friend.getWebsite(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("hometown", friend.getHometown().getName(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("current location", friend.getLocation().getName(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("timezone", friend.getTimezone().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(field);
		field= new Field("quotes", friend.getQuotes().toString(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		try {
			writer.addDocument(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	
	
    	
	System.out.println("DONE! " + getName());
    }
}