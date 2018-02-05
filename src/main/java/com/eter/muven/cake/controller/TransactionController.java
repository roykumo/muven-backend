package com.eter.muven.cake.controller;

import com.eter.cake.persistence.entity.InventoryOut;
import com.eter.cake.persistence.entity.Product;
import com.eter.cake.persistence.entity.ProductType;
import com.eter.cake.persistence.entity.rest.KeyValue;
import com.eter.cake.persistence.entity.rest.SellingReport;
import com.eter.cake.persistence.entity.rest.Transaction;
import com.eter.cake.persistence.service.InventoryOutDaoService;
import com.eter.cake.persistence.service.TransactionDaoService;
import com.eter.muven.cake.Constant;
import com.eter.response.CommonResponseGenerator;
import com.eter.response.entity.CommonPaging;
import com.eter.response.entity.CommonResponsePaging;
import com.eter.response.exception.UserException;
import com.eter.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(TransactionController.class);
    
	@SuppressWarnings("unused")
	private SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMM yyyy");

	@Autowired
	private TransactionDaoService transactionDaoService;

	@Autowired
	private InventoryOutDaoService inventoryOutDaoService;

	@Autowired
	private CommonResponseGenerator commonResponseGenerator;

    private static final SimpleDateFormat formatMonth = new SimpleDateFormat("MMM-yyyy");

    @RequestMapping(method = RequestMethod.GET, value = "/sell/report")
    @ResponseBody
    public String getSellingReport( @RequestParam(name = "type", required = true) String type,
                                    @RequestParam(name = "month", required = true) int month,
                                    @RequestParam(name = "year", required = true) int year) throws Exception {
        ProductType productType = new ProductType();
        productType.setId(type);

        List<SellingReport> sellingReportList = transactionDaoService.getSellingReport(productType, month, year);

        CommonPaging<SellingReport> paging = new CommonPaging<SellingReport>();
        paging.setPage(0);
        paging.setRowPerPage(10000);
        paging.setTotalData(sellingReportList!=null ? sellingReportList.size() : 0);
        paging.setData(sellingReportList);

        CommonResponsePaging<SellingReport> resp = new CommonResponsePaging<SellingReport>(paging);

        ObjectWriter writer = JsonUtil.generateDefaultJsonWriter();
        return writer.writeValueAsString(resp);
    }

	@RequestMapping(method = RequestMethod.GET, value = "/list")
	@ResponseBody
    public String getPaging( @RequestParam(name = "pageSize", defaultValue = "100000") String pageSize,
                                    @RequestParam(name = "pageNo", defaultValue = "0") String page,
                                    @RequestParam(name = "sortDir", defaultValue = Constant.ORDER_ASC) String sortDir,
                                    @RequestParam(name = "sort", defaultValue = Product.Constant.CREATED_DATE_FIELD) String sort,
                                    @RequestParam(name = "field", defaultValue = "") String field)
            throws Exception {

		List<KeyValue> filter;
		if(!StringUtils.isEmpty(field)){
			ObjectMapper mapper = new ObjectMapper();
			filter = mapper.readValue(field, new TypeReference<List<KeyValue>>(){});
			
			logger.debug("filter: {} - {}", filter.size(), JsonUtil.generateJson(filter));
		}else{
			filter = new ArrayList<>();
		}

        CommonPaging<Transaction> transactionCommonPaging = transactionDaoService.getTransactions(Integer.parseInt(pageSize), Integer.parseInt(page), sortDir, sort, filter);
        CommonResponsePaging<Transaction> resp = new CommonResponsePaging<Transaction>(transactionCommonPaging);

        ObjectWriter writer = JsonUtil.generateDefaultJsonWriter();
        return writer.writeValueAsString(resp);
    }

    @GetMapping(path = "/sell/report/print", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> printReceipt(@RequestParam(name = "type", required = true) String type,
                                               @RequestParam(name = "month", required = true) int month,
                                               @RequestParam(name = "year", required = true) int year) throws UserException{
        try{
            ProductType productType = new ProductType();
            productType.setId(type);

            List<SellingReport> sellingReportList = transactionDaoService.getSellingReport(productType, month, year);

            Map<String, Object> params = new HashMap<>();

            Calendar date = Calendar.getInstance();
            date.set(Calendar.DAY_OF_MONTH, 1);
            date.set(Calendar.MONTH, month-1);
            date.set(Calendar.YEAR, year);

            params.put("month", formatMonth.format(date.getTime()));

            JRBeanCollectionDataSource beanCollectionDs = new JRBeanCollectionDataSource(sellingReportList, false);
            params.put("ReportDataset", beanCollectionDs);

            return generatePDFReport("sellingReport", params);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/receipt", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> printReceipt(@RequestParam(name = "id" , required = true) String id) throws UserException{
        try{
            InventoryOut inventoryOut = inventoryOutDaoService.getById(id);

            if(inventoryOut==null){
                throw new UserException("X06", "Inventory Not Found");
            }

            Integer a = 1;
            BigDecimal b = new BigDecimal(0);

            System.out.println(b.multiply(new BigDecimal(a)));

            Map<String, Object> params = new HashMap<>();

            params.put(Constant.ReportParamReceipt.RECEIPT_NO, inventoryOut.getTransactionCode());
            params.put(Constant.ReportParamReceipt.TYPE, inventoryOut.getType());
            params.put(Constant.ReportParamReceipt.USER, "");
            params.put(Constant.ReportParamReceipt.DATE, inventoryOut.getDate());
            params.put(Constant.ReportParamReceipt.TOTAL_PRICE, inventoryOut.getTotalPrice());
            params.put(Constant.ReportParamReceipt.PAYMENT, inventoryOut.getPayment());

            if(inventoryOut.getPayment()!=null && inventoryOut.getPayment().getPayAmount()!=null){
                params.put(Constant.ReportParamReceipt.PAY_AMOUNT, inventoryOut.getPayment().getPayAmount());
                params.put(Constant.ReportParamReceipt.CHANGE, inventoryOut.getPayment().getPayAmount().subtract(inventoryOut.getTotalPrice()));
            }else{
                params.put(Constant.ReportParamReceipt.PAY_AMOUNT, new BigDecimal(0));
                params.put(Constant.ReportParamReceipt.CHANGE, new BigDecimal(0));
            }

            JRBeanCollectionDataSource beanCollectionDs = new JRBeanCollectionDataSource(
                    inventoryOut.getItems(), false);

            params.put(Constant.ReportParamReceipt.DATA_SET, beanCollectionDs);

            return generatePDFReport(Constant.ReportParamReceipt.REPORT_NAME, params);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected ResponseEntity<byte[]> generatePDFReport(String reportName, Map<String,Object> params) throws UserException {
        try {
            //return JasperReportUtil.getPdfResponseEntity(reportName, reportName, false, params, null);

            byte[] report = doGetReport(reportName, params);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            headers.setContentDispositionFormData("inline", reportName + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return ResponseEntity
                    .ok()
                    .contentLength(report.length)
                    .headers(headers)
                    .body(report);
        } catch (Exception e) {
            logger.error("error receipt", e);
            throw new UserException("X06", "Error occurred while generating Report");
        }
    }

    public byte[] doGetReport(String reportName, Map<String, Object> parameters) {
        InputStream jrxmlInput = null;
        try {
            jrxmlInput = this.getClass().getClassLoader().getResource("report/" + reportName + ".jrxml").openStream();
            JasperReport report = JasperCompileManager.compileReport(jrxmlInput);
            JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(print);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (JRException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
