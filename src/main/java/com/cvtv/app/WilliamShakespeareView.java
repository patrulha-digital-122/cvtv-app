package com.cvtv.app;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route( "williamshakespeare" )
@PageTitle( "Equipa William Shakespeare :: Código" )
@PWA(	name = "Equipa William Shakespeare",
		shortName = "W. Shakespeare",
		startPath = "williamshakespeare",
		backgroundColor = "#227aef",
		themeColor = "#227aef",
		offlinePath = "offline-page.html",
		offlineResources =
		{ "images/offline-login-banner.jpg" },
		enableInstallPrompt = true,
		display = "standalone",
		description = "Esta app altera texto usando o codigo da Equipa William Shakespeare" )
public class WilliamShakespeareView extends VerticalLayout
{
	private static final long	serialVersionUID	= -8783462098490997667L;
	private final Logger		logger				= LoggerFactory.getLogger( getClass() );
	private final TextArea		textArea;
	private final TextArea		textAreaCodificado;
	private final Button		btnCodificar;
	private final Checkbox		cbManterAcentos;

	public WilliamShakespeareView()
	{
		setAlignItems( Alignment.CENTER );
		setJustifyContentMode( JustifyContentMode.CENTER );

		setMargin( false );
		setSizeFull();

		textArea = new TextArea( "Texto a codificar" );
		textArea.setSizeFull();

		btnCodificar = new Button( "Codificar" );
		btnCodificar.setWidth( "100%" );
		btnCodificar.addClickListener( e -> codificarTexto() );

		cbManterAcentos = new Checkbox( "Utilizar acentos" );
		cbManterAcentos.setValue( false );
		cbManterAcentos.setWidth( "100%" );

		textAreaCodificado = new TextArea( "Texto codificado" );
		textAreaCodificado.setEnabled( false );
		textAreaCodificado.setSizeFull();

		add( textArea, btnCodificar, cbManterAcentos, textAreaCodificado );

		setFlexGrow( 4, textArea );
		setFlexGrow( 1, btnCodificar );
		setFlexGrow( 1, cbManterAcentos );
		setFlexGrow( 4, textAreaCodificado );
	}

	protected void codificarTexto()
	{
		final String strOriginalValue = textArea.getValue();
		final StringBuilder sb = new StringBuilder();
		for ( final String strWord : StringUtils.split( strOriginalValue ) )
		{
			String strWordWithouAccents = strWord;
			if ( !cbManterAcentos.getValue() )
			{
				strWordWithouAccents = StringUtils.stripAccents( strWord );
			}
			final String randomAlphabetic = RandomStringUtils.randomAlphabetic( 1 );
			if ( StringUtils.isNumeric( strWordWithouAccents ) )
			{
				sb.append( randomAlphabetic );
				for ( final char c : strWordWithouAccents.toCharArray() )
				{
					final Integer iCValue = Integer.valueOf( "" + c );
					sb.append( "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring( iCValue, iCValue + 1 ) );
				}
				sb.append( randomAlphabetic );
			}
			else
			{
				String randomAlphabeticLast = RandomStringUtils.randomAlphabetic( 1 );
				while ( randomAlphabetic.equals( randomAlphabeticLast ) )
				{
					randomAlphabeticLast = RandomStringUtils.randomAlphabetic( 1 );
				}
				sb.append( randomAlphabetic );
				sb.append( StringUtils.reverse( strWordWithouAccents ) );
				sb.append( randomAlphabeticLast );
			}
			sb.append( " " );
		}

		textAreaCodificado.setValue( sb.toString().toLowerCase() );
	}
}
