package com.study.springbatch.remotepartition;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.study.springbatch.remotepartition.domain.ColumnRangePartitioner;
import com.study.springbatch.remotepartition.domain.Customer;
import com.study.springbatch.remotepartition.domain.CustomerRowMapper;

@Configuration
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Bean
	public Job job() {

		return jobBuilderFactory.get("job1").start(step1()).build();

	}

	@Bean
	public ColumnRangePartitioner partitioner() {
		ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
		partitioner.setColumn("id");
		partitioner.setDataSource(dataSource);
		partitioner.setTable("customer");
		return partitioner;
	}

	@Bean
	public Step step1() {
		// TODO Auto-generated method stub
		return stepBuilderFactory.get("step1").partitioner(slaveStep().getName(), partitioner()).step(slaveStep())
				.gridSize(4).taskExecutor(new SimpleAsyncTaskExecutor()).build();
	}

	@Bean
	public Step slaveStep() {
		return stepBuilderFactory.get("slaveStep").<Customer, Customer>chunk(1000).reader(pagingItemReader(null, null))
				.writer(customerItemWriter()).build();
	}

	@Bean
	@StepScope
	public ItemWriter<Customer> customerItemWriter() {
		JdbcBatchItemWriter<Customer> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
		jdbcBatchItemWriter.setDataSource(dataSource);
		jdbcBatchItemWriter.setSql("INSERT INTO NEW_CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		return jdbcBatchItemWriter;
	}

	// @Value("#
	// {stepExecutionContext['maxValue']}")

	@Bean
	@StepScope
	public ItemReader<Customer> pagingItemReader(
			@Value("#{stepExecutionContext['minValue']}") Long minValue,
			@Value("#{stepExecutionContext['maxValue']}") Long maxValue) {
		// TODO Auto-generated method stub

		System.out.println(" starting with min max value " + minValue + "   " + maxValue);
		JdbcPagingItemReader<Customer> jdbcPagingItemReader = new JdbcPagingItemReader<>();
		jdbcPagingItemReader.setDataSource(dataSource);
		jdbcPagingItemReader.setFetchSize(1000);
		jdbcPagingItemReader.setRowMapper(new CustomerRowMapper());
		MySqlPagingQueryProvider mySqlPagingQueryProvider = new MySqlPagingQueryProvider();
		mySqlPagingQueryProvider.setFromClause("from customer");
		mySqlPagingQueryProvider.setSelectClause("id,firstname,lastname,birthdate");
		mySqlPagingQueryProvider.setWhereClause("where id >= " + minValue + " and id <= " + maxValue);
		Map<String, Order> sortKeys = new HashMap(1);
		sortKeys.put("id", Order.ASCENDING);
		mySqlPagingQueryProvider.setSortKeys(sortKeys);
		jdbcPagingItemReader.setQueryProvider(mySqlPagingQueryProvider);
		return jdbcPagingItemReader;
	}

}
