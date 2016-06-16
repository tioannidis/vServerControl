using Uno;
using Uno.Collections;
using Fuse;
using Fuse.Reactive;
using Fuse.Scripting;
using Fuse.Controls;
using Uno.Compiler.ExportTargetInterop;
using Android.android.app;

[TargetSpecificImplementation]
public class ModalJS : NativeModule
{
	public ModalJS () {
		AddMember(new NativeFunction("showModal", (NativeCallback)ShowModal));
	}
 
	Panel myUXModal;
	Panel myBasePanel;

	Panel UXModal(string pTitle, string text, Fuse.Scripting.Array pButtons) {
		var lPanel = new Fuse.Controls.Panel();
		//
		// DockPanel
		var lDockPanel = new Fuse.Controls.DockPanel();
		lDockPanel.Alignment = Fuse.Elements.Alignment.VerticalCenter;
		lDockPanel.Background = new Fuse.Drawing.StaticSolidColor(Fuse.Drawing.Colors.Black);
		//
		// Title-StackPanel
		var lTitleStackPanel = new Fuse.Controls.StackPanel();
		lDockPanel.Children.Add(lTitleStackPanel);
		global::Fuse.Controls.DockPanel.SetDock(lTitleStackPanel, Fuse.Layouts.Dock.Top);
		//
		// Title
		var lTitle = new Fuse.Controls.Text();
		lTitleStackPanel.Children.Add(lTitle);
		lTitle.Value = pTitle;
		lTitle.TextWrapping = Fuse.Controls.TextWrapping.Wrap;
		lTitle.FontSize = 18f;
		lTitle.TextAlignment = Fuse.Controls.TextAlignment.Left;
		lTitle.TextColor = Fuse.Drawing.Colors.Blue;
		lTitle.Margin = float4(10f, 10f, 10f, 10f);
		//
		// Title-HorizontalBar
		var lTitleHorizontalBar = new Fuse.Controls.Panel();
		lTitleStackPanel.Children.Add(lTitleHorizontalBar);
		lTitleHorizontalBar.Margin = float4(0f, 5f, 0f, 5f);
		lTitleHorizontalBar.Height = 1f;
		lTitleHorizontalBar.Background = new Fuse.Drawing.StaticSolidColor(Fuse.Drawing.Colors.Blue);
		//
		// Text-ScrollView
		var lScrollView = new Fuse.Controls.ScrollView();
		lDockPanel.Children.Add(lScrollView);
		//
		// Text
		var lText = new Fuse.Controls.Text();
		lScrollView.Content = lText;
		lText.Value = text;
		lText.TextWrapping = Fuse.Controls.TextWrapping.Wrap;
		lText.FontSize = 18f;
		lText.TextAlignment = Fuse.Controls.TextAlignment.Left;
		lText.TextColor = Fuse.Drawing.Colors.White;
		lText.Margin = float4(10f, 10f, 10f, 10f);
		//
		// Buttons
		var lButtonGrid = new Fuse.Controls.Grid();
		lDockPanel.Children.Add(lButtonGrid);
		lButtonGrid.ColumnCount = pButtons.Length;
		lButtonGrid.Margin = float4(10f, 0f, 10f, 10f);
		global::Fuse.Controls.DockPanel.SetDock(lButtonGrid, Fuse.Layouts.Dock.Bottom);
		for (var i = 0; i < pButtons.Length; i++) {
			var lButton = new Fuse.Controls.Button();
			lButtonGrid.Children.Add(lButton);
			lButton.Text = pButtons[i] as string;
			Fuse.Gestures.Clicked.AddHandler(lButton, ButtonClickHandler);
		}
		//
		var lPanelRahmen = new Fuse.Controls.Rectangle();
		lPanelRahmen.Background = new Fuse.Drawing.StaticSolidColor(float4(0f, 0f, 0f, 0.14f));
		lPanel.Children.Add(lDockPanel);
		lPanel.Children.Add(lPanelRahmen);
		lPanel.HitTestMode = Fuse.Elements.HitTestMode.LocalBoundsAndChildren;
		return lPanel;
	}

	void ButtonClickHandler (object pSender, Fuse.Gestures.ClickedArgs pArguments) {
		var lButton = pSender as Button;
		running = false;
		UpdateManager.PostAction(RemoveModalUX);
		Context.Dispatcher.Invoke(new InvokeEnclosure(callback, lButton.Text).InvokeCallback);
	}

	class InvokeEnclosure {

		public InvokeEnclosure (Fuse.Scripting.Function func, string cbtext) {
			callback = func;
			callback_text = cbtext;
		}
		
		Fuse.Scripting.Function callback;
		string callback_text;

		public void InvokeCallback () {
			callback.Call(callback_text);
		}
	}

	List<Node> ChildrenBackup;
	
	void AddModalUX() {
		var lChildren = myBasePanel.Children;
		ChildrenBackup = new List<Node>();
		for (int i = 0; i < lChildren.Count; i++) {
			var lChild = lChildren[i];
			ChildrenBackup.Add(lChild);
		}
		myBasePanel.Children.Clear();
		myBasePanel.Children.Add(myUXModal);
	}

	void RemoveModalUX() {
		myBasePanel.Children.Clear();
		for (int i=0; i< ChildrenBackup.Count; i++) {
			myBasePanel.Children.Add(ChildrenBackup[i]);
		}
	}

	extern(iOS)
	void iOSClickHandler (int id) {
		running = false;
		var s = buttons[id] as string;
		Context.Dispatcher.Invoke(new InvokeEnclosure(callback, s).InvokeCallback);
	}

	extern(Android)
	void AndroidClickHandler (string s) {
		running = false;
		Context.Dispatcher.Invoke(new InvokeEnclosure(callback, s).InvokeCallback);
	}

	Context Context;
	Fuse.Scripting.Function callback;
	bool running = false;
	Fuse.Scripting.Array buttons;
	string title;
	string body;

	[TargetSpecificImplementation]
	extern(iOS)
	public void ShowImpl(iOS.UIKit.UIViewController controller, ObjC.ID alert, string[] buttons);

	extern(iOS)
	public void ShowModaliOS() {
		if (title == "HACKETIHACK") {
			iOSClickHandler(-1);
		}
		var alert = iOS.UIKit.UIAlertController._alertControllerWithTitleMessagePreferredStyle(
			title,
			body,
			iOS.UIKit.UIAlertControllerStyle.UIAlertControllerStyleAlert
		);

		var s_buttons = new string[buttons.Length];
		for (var i = 0; i < buttons.Length; i++) {
			s_buttons[i] = buttons[i] as string;
		}

		// var alert_uivc = new iOS.UIKit.UIAlertController(alert);
		//var action = new iOS.UIKit.UIAlertAction();
		// action.Title = "OK";

		var uivc = iOS.UIKit.UIApplication._sharedApplication().KeyWindow.RootViewController;
		ShowImpl(uivc, alert, s_buttons);
		// uivc.presentModalViewControllerAnimated(alert_uivc, false);
	}

	extern(Android)
	public void ShowModalAndroid() {
		// Might want to throw error if more than 3 buttons
		var ctx = Android.android.app.Activity.GetAppActivity();
		var alert = new AlertDialogDLRBuilder(ctx);
		Android.java.lang.String a_title = title;
		alert.setTitle(a_title);
		alert.setCancelable(false);
		Android.java.lang.String a_body = body;
		alert.setMessage(a_body);

		for (var i = 0; i < buttons.Length; i++) {
			var s = buttons[i] as string;
			Android.java.lang.String a_but = s;
			var clickhandler = new AndroidListener(s, AndroidClickHandler);
			if (i == 0) {
				alert.setNegativeButton(a_but, clickhandler);
			}
			else if ((i == 1)&&(buttons.Length>2)) {
				alert.setNeutralButton(a_but, clickhandler);
			}
			else {
				alert.setPositiveButton(a_but, clickhandler);
			}
		}
		alert.show();
	}

	object ShowModal (Context c, object[] args) {
		if (running) return null;
		running = true;
		title = args[0] as string;
		body = args[1] as string;
		buttons = args[2] as Fuse.Scripting.Array;
		callback = args[3] as Fuse.Scripting.Function;
		Context = c;

		Uno.Diagnostics.Debug.Alert(body);
		if defined(iOS) {
			UpdateManager.PostAction(ShowModaliOS);
			return null;
		}
		else if defined(Android) {
			UpdateManager.PostAction(ShowModalAndroid);
			return null;
		}
		else {
			myBasePanel = FindPanel(AppBase.Current.RootViewport);
			myUXModal = UXModal(title, body, buttons);
			UpdateManager.PostAction(AddModalUX);
			return null;
		}

	}

	Panel FindPanel (Node n) {
		if defined(CIL) {
			if (n is Outracks.Simulator.FakeApp) {
				var a = n as Outracks.Simulator.FakeApp;
				var c = a.Children[1];
				return FindPanel(c);
			}
		}
		if (n is Fuse.Controls.Panel) {
			var p = n as Fuse.Controls.Panel;
			return p;
		}
		return null;
	}
}