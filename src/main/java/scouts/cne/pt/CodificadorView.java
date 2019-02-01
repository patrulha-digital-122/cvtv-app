package scouts.cne.pt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.StreamResource;
import scouts.cne.pt.model.CodigoTTF;

@Route( value = "" )
@PageTitle( "Gerador de Códigos" )
@PWA(	name = "Gerador de Codigos",
		shortName = "CNE - Codigos",
		startPath = "",
		backgroundColor = "#227aef",
		themeColor = "#227aef",
		offlinePath = "offline-page.html",
		offlineResources =
		{ "images/offline-login-banner.png" },
		enableInstallPrompt = true,
		display = "standalone",
		description = "Esta app permite codificar texto utilizando varias tipos de fontes" )
@StyleSheet( "frontend://css/styles.css" )
public class CodificadorView extends VerticalLayout
{
	private static final long			serialVersionUID	= -8783462098490997667L;
	private final Logger				logger				= LoggerFactory.getLogger( getClass() );
	private final TextArea				textArea;
	private final TextArea				textAreaCodificado;
	private final ComboBox< CodigoTTF >	cbCodigos;
	private final Anchor				anchorDownload;

	public CodificadorView()
	{
		setAlignItems( Alignment.CENTER );
		setJustifyContentMode( JustifyContentMode.CENTER );
		setSizeFull();

		textArea = new TextArea( "Texto a codificar" );
		textArea.setClassName( "desc", true );
		textArea.setSizeFull();
		textArea.setValueChangeMode( ValueChangeMode.EAGER );
		textArea.addValueChangeListener( e -> codificarTexto() );

		cbCodigos = new ComboBox<>( "Escolher o codigo" );
		cbCodigos.setWidth( "100%" );

		List< CodigoTTF > lstCodigos = new ArrayList<>();
		lstCodigos.add( new CodigoTTF( "Angular", "angular" ) );
		lstCodigos.add( new CodigoTTF( "Homografo", "homografo" ) );
		lstCodigos.add( new CodigoTTF( "LGP", "lgp" ) );
		lstCodigos.add( new CodigoTTF( "Morse", "morse" ) );
		lstCodigos.add( new CodigoTTF( "Chinês", "chines" ) );

		cbCodigos.setItemLabelGenerator( e -> e.getName() );
		cbCodigos.setItems( lstCodigos );
		cbCodigos.setValue( lstCodigos.get( 0 ) );

		textAreaCodificado = new TextArea();
		textAreaCodificado.addThemeVariants( TextAreaVariant.LUMO_ALIGN_CENTER );
		textAreaCodificado.getStyle().set( "font-size", "1.75rem" );
		textAreaCodificado.setClassName( lstCodigos.get( 0 ).getUrl() );
		textAreaCodificado.setEnabled( false );
		textAreaCodificado.setSizeFull();

		anchorDownload = new Anchor( createResource(), "" );
		anchorDownload.getElement().setAttribute( "download", true );
		anchorDownload.setSizeFull();
		Button button = new Button( "Ficheiro .ttf", VaadinIcon.DOWNLOAD.create() );
		button.setSizeFull();
		anchorDownload.add( button );

		cbCodigos.addValueChangeListener( event ->
		{
			textAreaCodificado.setClassName( event.getValue().getUrl(), true );
			codificarTexto();
			anchorDownload.setHref( createResource() );
		} );

		HorizontalLayout optionsLayout = getOptionsLayout();
		add( textArea, optionsLayout, textAreaCodificado );

		setFlexGrow( 4, textArea );
		// setFlexGrow( 1, btnCodificar );
		setFlexGrow( 1, optionsLayout );
		setFlexGrow( 4, textAreaCodificado );
	}

	/**
	 * The <b>createResource</b> method returns {@link Object}
	 * 
	 * @author 62000465 2019-02-01
	 * @return
	 */
	private StreamResource createResource()
	{
		CodigoTTF value = cbCodigos.getValue();
		String strFileName = value.getUrl() + ".ttf";
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File file = new File( classLoader.getResource( "static/frontend/fonts/" + strFileName ).getFile() );
		return new StreamResource( strFileName, () ->
		{
			try
			{
				return new ByteArrayInputStream( FileUtils.readFileToByteArray( file ) );
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
			return null;
		} );
	}

	protected void codificarTexto()
	{
		textAreaCodificado.setValue( StringUtils.stripAccents( textArea.getValue() ) );
	}
	
	private HorizontalLayout getOptionsLayout() {

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing( true );
		horizontalLayout.setMargin( false );
		horizontalLayout.setPadding( true );
		horizontalLayout.setWidth( "100%" );
		
		horizontalLayout.setAlignItems( Alignment.CENTER );
		horizontalLayout.setJustifyContentMode( JustifyContentMode.CENTER );

		// horizontalLayout.setSizeFull();
		
		Button btnIncreaseText = new Button( "Texto", VaadinIcon.PLUS.create() );
		btnIncreaseText.addClickListener( e -> textAreaCodificado.getStyle().set( "font-size", processTextSize( true ) ) );
		btnIncreaseText.setSizeFull();
		Button btnDecreaseText = new Button( "Texto", VaadinIcon.MINUS.create() );
		btnDecreaseText.addClickListener( e -> textAreaCodificado.getStyle().set( "font-size", processTextSize( false ) ) );
		btnDecreaseText.setSizeFull();
		
		horizontalLayout.add( cbCodigos, btnIncreaseText, btnDecreaseText, anchorDownload );
		
		return horizontalLayout;
	}

	private String processTextSize( boolean bEncrease )
	{
		String strOriginalSize = textAreaCodificado.getStyle().get( "font-size" );
		if ( StringUtils.isBlank( strOriginalSize ) )
		{
			return "1.75rem";
		}

		strOriginalSize = strOriginalSize.replace( "rem", "" );

		BigDecimal bigDecimal = new BigDecimal( strOriginalSize );
		BigDecimal valueToChange = BigDecimal.valueOf( 0.25 );

		if ( bEncrease )
		{
			bigDecimal = bigDecimal.add( valueToChange );
		}
		else
		{
			bigDecimal = bigDecimal.subtract( valueToChange );
		}
		return bigDecimal.toString() + "rem";
	}
}
