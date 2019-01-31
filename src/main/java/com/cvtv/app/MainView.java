package com.cvtv.app;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route( "cvtv" )
@PageTitle( "CVTV :: app" )
public class MainView extends VerticalLayout
{
	private static final long					serialVersionUID	= -8783462098490997667L;
	private final Logger						logger				= LoggerFactory.getLogger( getClass() );
	private final List< Person >				data				= new ArrayList<>();
	private final VerticalLayout				gridLayout			= new VerticalLayout();
	private final MemoryBuffer					fileBuffer			= new MemoryBuffer();
	public static final Map< Integer, String >	headerRowMap		= new HashMap<>();
	private final TextArea						textArea			= new TextArea();
	private final TextField						emailText			= new TextField( "Coluna com E-mail", "E-mail" );
	private final TextField						nomeText			= new TextField( "Coluna com o Nome", "Nome" );
	private final Set< Person >					selectedItems		= new HashSet<>();

	public MainView( @Autowired MessageBean bean )
	{
		final Button button = new Button( "Mailing list", e -> showEmailsWindow() );
		button.setWidth( "100%" );

		final Upload upload = new Upload( fileBuffer );
		upload.setMaxFiles( 1 );
		upload.setWidth( "100%" );

		textArea.setSizeFull();
		textArea.setEnabled( false );
		textArea.getStyle().set( "overflow", "auto" );

		emailText.setValue( "E-mail" );
		emailText.setWidth( "100%" );
		nomeText.setValue( "Nome" );
		nomeText.setWidth( "100%" );

		gridLayout.setSizeFull();

		upload.addSucceededListener( event ->
		{
			logger.info( "Succeded: " + fileBuffer.getFileData().getFileName() );
			updateGrid();
		} );

		setAlignItems( Alignment.CENTER );
		add( upload, gridLayout, button );
	}

	private Object showEmailsWindow()
	{
		final Dialog dialog = new Dialog();

		final VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin( false );
		verticalLayout.setSizeFull();
		final ValueChangeListener< ValueChangeEvent< ? > > changeListener = event -> updateTextArea();
		emailText.addValueChangeListener( changeListener );
		nomeText.addValueChangeListener( changeListener );
		updateTextArea();
		verticalLayout.add( emailText, nomeText, textArea );
		dialog.add( verticalLayout );
		dialog.setWidth( "400px" );
		dialog.setHeight( "600px" );

		dialog.open();
		return null;
	}

	private void updateTextArea()
	{
		final StringBuilder sb = new StringBuilder();

		for ( final Person person : selectedItems )
		{
			final String value = ( String ) person.getValue( emailText.getValue() );

			if ( StringUtils.isNotBlank( value ) && EmailValidator.getInstance().isValid( value ) )
			{
				if ( sb.length() > 0 )
				{
					sb.append( ", " );
				}
				// if ( chbWithNames.getValue() )
				// {
				sb.append( "\"" );
				sb.append( person.getValue( nomeText.getValue() ) );
				sb.append( "\" " );
				// }
				sb.append( "<" );
				sb.append( value );
				sb.append( ">" );
			}
		}

		textArea.setValue( sb.toString() );

	}

	private void updateGrid()
	{
		data.clear();
		headerRowMap.clear();

		gridLayout.removeAll();

		final Grid< Person > grid = new Grid<>( Person.class );
		grid.setSelectionMode( SelectionMode.MULTI );
		grid.setColumnReorderingAllowed( true );
		grid.setMultiSort( true );

		grid.addSelectionListener( event ->
		{
			selectedItems.clear();
			selectedItems.addAll( event.getAllSelectedItems() );
		} );

		try ( final Workbook workbook = new XSSFWorkbook( fileBuffer.getInputStream() ) )
		{
			final Sheet datatypeSheet = workbook.getSheetAt( 0 );
			final Iterator< Row > iterator = datatypeSheet.iterator();
			if ( iterator.hasNext() )
			{
				final Row headerRow = iterator.next();

				final Iterator< Cell > cellIterator = headerRow.cellIterator();
				while ( cellIterator.hasNext() )
				{
					final Cell cell = cellIterator.next();
					final String strNormalize =
									Normalizer.normalize( cell.getStringCellValue(), Normalizer.Form.NFD ).replaceAll( "[^\\p{ASCII}]", "" );
					headerRowMap.put( cell.getColumnIndex(), strNormalize );
				}
			}

			while ( iterator.hasNext() )
			{
				final Row row = iterator.next();
				final Iterator< Cell > cellIterator = row.cellIterator();
				final Person person = new Person();
				while ( cellIterator.hasNext() )
				{
					final Cell cell = cellIterator.next();
					switch ( cell.getCellType() )
					{
						case BLANK:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), "" );
							break;
						case STRING:
						case FORMULA:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), cell.getStringCellValue() );
							break;
						case BOOLEAN:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), String.valueOf( cell.getBooleanCellValue() ) );
							break;
						case NUMERIC:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), String.valueOf( cell.getNumericCellValue() ) );
							break;
						default:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), "?" );
							break;
					}
				}
				if ( !person.isBlank() )
				{
					data.add( person );
				}
			}

			final ListDataProvider< Person > fromStream = DataProvider.fromStream( data.stream() );
			grid.setDataProvider( fromStream.withConfigurableFilter() );
			if ( !headerRowMap.isEmpty() )
			{

				headerRowMap.values().forEach( col ->
				{
					// grid.removeColumnByKey( col );
					grid.addColumn( source -> source.getValue( col ) ).setResizable( true ).setSortable( true ).setHeader( col );
				} );
			}

		}
		catch ( final Exception e )
		{
			logger.error( e.getMessage(), e );
		}

		gridLayout.add( grid );
	}

}
