/*
 * Crée par SharpDevelop.
 * Utilisateur: ubiquarium
 * Date: 27/07/2012
 * Heure: 11:35
 * 
 * Pour changer ce modèle utiliser Outils | Options | Codage | Editer les en-têtes standards.
 */
using System;
using WComp.Beans;

namespace WComp.Beans
{
	/// <summary>
	/// This is a sample bean, which has an integer evented property and a method.
	/// 
	/// Notes: for beans creating threads, the IThreadCreator interface should be implemented,
	/// 	providing a cleanup method should be implemented and named `Stop()'.
	/// For proxy beans, the IProxyBean interface should  be implemented,
	/// 	providing the IsConnected property, allowing the connection status to be drawn in
	/// 	the AddIn's graphical designer.
	/// 
	/// Several classes can be defined or used by a Bean, but only the class with the
	/// [Bean] attribute will be available in WComp. Its ports will be all public methods,
	/// events and properties definied in that class.
	/// </summary>
	[Bean(Category="MyCategory")]
	public class metachecker
	{
		/// <summary>
		/// Fill in private attributes here.
		/// </summary>
		private bool actionbool;
		private int actionint;
		private String metadata;
		private bool fireOnce = false;
		private bool MDfired = false;

		/// <summary>
		/// This property will appear in bean's property panel and bean's input functions.
		/// </summary>
		
		public void resetMDfired(){
			MDfired = false;
		}
		
		public bool MyfireOnce {
			get { return fireOnce; }
			set {
				fireOnce = value;
			}
		}
		public bool Myactionbool {
			get { return actionbool; }
			set {
				actionbool = value;
			}
		}
		public int Myactionint {
			get { return actionint; }
			set {
				actionint = value;
			}
		}
		public String Mymetadata {
			get { return metadata; }
			set {
				metadata = value;
				
				if(!fireOnce || !MDfired){
					FireMDEvent(metadata);
					MDfired = true;
				}
			}
		}
		
		public void checkMetaData(String md){
			String[] cond = metadata.Split('&');
	       	Boolean ok = true;
	
	       	foreach (string word in cond){
	       		if(md.IndexOf(word)==-1){
	       			ok = false;
	       			break;
	       		}		
	       	}
	       	
			if(ok){
				FireActionEvent();
			}
		}
		

		/// <summary>
		/// Here are the delegate and his event.
		/// A function checking nullity should be used to fire events (like FireIntEvent).
		/// </summary>
		public delegate void MetadataEventHandler(String str);
		/// <summary>
		/// the following declaration is the event by itself. Its name, here "PropertyChanged",
		/// is the name of the event as it will be displayed in the bean type's interface.
		/// </summary>
		public event MetadataEventHandler MDChanged;
		
		
		private void FireMDEvent(String str) {
			if (MDChanged != null)
				MDChanged(str);
		}
		
		public delegate void ActionboolEventHandler(bool b);
		public delegate void ActionintEventHandler(int i);

		public event ActionboolEventHandler MakeActionbool;
		public event ActionintEventHandler MakeActionint;
		
		private void FireActionEvent() {
			if (MakeActionbool != null)
				MakeActionbool(actionbool);
			if (MakeActionint != null)
				MakeActionint(actionint);
		}
	}
}
