package scouts.cne.pt;

import java.math.BigDecimal;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route( value= "williamshakespeare" )
@PageTitle( "Equipa William Shakespeare :: Código" )
public class WilliamShakespeareView extends VerticalLayout
{
	private static final long	serialVersionUID	= -8783462098490997667L;
	private final Logger		logger				= LoggerFactory.getLogger( getClass() );
	private final TextArea		textArea;
	private final TextArea		textAreaCodificado;
	private final Checkbox		cbManterAcentos;

	public WilliamShakespeareView()
	{
		setAlignItems( Alignment.CENTER );
		setJustifyContentMode( JustifyContentMode.CENTER );

		setMargin( false );
		setSizeFull();

		textArea = new TextArea( "Texto a codificar" );
		textArea.setValueChangeMode( ValueChangeMode.EAGER );
		textArea.addValueChangeListener( e -> codificarTexto() );
		textArea.setSizeFull();

		cbManterAcentos = new Checkbox( "Utilizar acentos" );
		cbManterAcentos.setValue( false );
		cbManterAcentos.setWidth( "100%" );

		textAreaCodificado = new TextArea();
		textAreaCodificado.addThemeVariants( TextAreaVariant.LUMO_ALIGN_CENTER );
		textAreaCodificado.getStyle().set( "font-size", "1.75rem" );
		textAreaCodificado.setEnabled( false );
		textAreaCodificado.setSizeFull();

		HorizontalLayout optionsLayout = getOptionsLayout();

		add( textArea, optionsLayout, textAreaCodificado );

		setFlexGrow( 4, textArea );
		setFlexGrow( 1, optionsLayout );
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

	private HorizontalLayout getOptionsLayout() {

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing( true );
		horizontalLayout.setMargin( false );
		horizontalLayout.setPadding( true );
		horizontalLayout.setWidth( "100%" );
		
		horizontalLayout.setAlignItems( Alignment.CENTER );
		horizontalLayout.setJustifyContentMode( JustifyContentMode.CENTER );

		Button btnCodificar = new Button( "Codificar", VaadinIcon.CODE.create() );
		btnCodificar.addClickListener( e -> codificarTexto() );
		btnCodificar.setSizeFull();
		
		Button btnIncreaseText = new Button( "Texto", VaadinIcon.PLUS.create() );
		btnIncreaseText.addClickListener( e -> textAreaCodificado.getStyle().set( "font-size", processTextSize( true ) ) );
		btnIncreaseText.setSizeFull();
		Button btnDecreaseText = new Button( "Texto", VaadinIcon.MINUS.create() );
		btnDecreaseText.addClickListener( e -> textAreaCodificado.getStyle().set( "font-size", processTextSize( false ) ) );
		btnDecreaseText.setSizeFull();
		
		horizontalLayout.add( cbManterAcentos, btnCodificar, btnIncreaseText, btnDecreaseText );
		
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
