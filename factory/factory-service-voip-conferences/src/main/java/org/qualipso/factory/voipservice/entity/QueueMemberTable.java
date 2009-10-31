package org.qualipso.factory.voipservice.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @author <a href="mailto:debe@man.poznan.pl">Marcin Wrzos</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */

@IdClass(QueueMemberTable.QueueMemberPK.class)
@Entity
@Table(name = "queue_member_table")
public class QueueMemberTable {

	public static class QueueMemberPK implements Serializable {
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = -4691245965052837272L;

		@Id
		@Column(nullable = false, length = 128)
		private String queue_name;

		@Id
		@Column(nullable = false, length = 128, name = "interface")
		private String interface_field;

		public QueueMemberPK() {}

		public int hashCode() {
			return (int) (queue_name+interface_field).hashCode();
		}

		public boolean equals(Object obj)
		{
			if (obj == this) return true;
			if (obj == null) return false;
			if (!(obj instanceof QueueMemberPK)) return false;
			QueueMemberPK pk = (QueueMemberPK) obj;
			return pk.queue_name.equals(queue_name) && pk.interface_field.equals(interface_field);
		}

		public String getInterface_field() {
			return interface_field;
		}

		public void setInterface_field(String interface_field) {
			this.interface_field = interface_field;
		}

		public String getQueue_name() {
			return queue_name;
		}

		public void setQueue_name(String queue_name) {
			this.queue_name = queue_name;
		}
	}

	@Id
	@Column(nullable = false, length = 128)
	private String queue_name;

	@Id
	@Column(nullable = false, length = 128, name = "interface")
	private String interface_field;

	private Long penalty;

	public QueueMemberTable() {}

	public String getInterface_field() {
		return interface_field;
	}

	public void setInterface_field(String interface_field) {
		this.interface_field = interface_field;
	}

	public Long getPenalty() {
		return penalty;
	}

	public void setPenalty(Long penalty) {
		this.penalty = penalty;
	}

	public String getQueue_name() {
		return queue_name;
	}

	public void setQueue_name(String queue_name) {
		this.queue_name = queue_name;
	}
}